let bookId = null;

document.addEventListener("DOMContentLoaded", () => { 
    bookId = getQueryParam("id");
    if (!bookId) {
        console.error("No book ID specified");
        return;
    }

    const url = `http://localhost:8080/api/books/${bookId}`;

    fetch(url)
        .then((response) => {
            if (!response.ok) {
                alert("Network response was not OK!");
                throw new Error("Network response was not OK!");
            }
            return response.json();
        })
        .then(data => {
            displayBookDetails(data);
            updateLinkVisibility(data.noOfAvailableCopies);
        })
        .catch(error => { 
            alert('Unexpected error.');
            console.error('Unexpected error:', error);
        });
    
    // setup the current holders link (click even listener)
    const currentHoldersLink = document.getElementById('currentHoldersLink');
    currentHoldersLink.addEventListener('click', (ev) => { 
        ev.preventDefault();
        fetchCurrentHolders(bookId);
    });
});

const getQueryParam = (param) => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(param);
};

const displayBookDetails = (book) => {
    console.log('Book: ', book);

    document.getElementById('bookTitleAndAuthor').textContent = `${book.title}, ${book.author}`;

    document.getElementById('isbn').textContent = book.isbn;
    document.getElementById('publisher').textContent = book.publisher;
    document.getElementById('yearOfPublication').textContent = book.yearOfPublication;
    document.getElementById('placePublished').textContent = book.placeOfPublication;
    document.getElementById('availableCopies').textContent = book.noOfAvailableCopies;
    document.getElementById('availableCopies').textContent = book.noOfAvailableCopies;
    
}

const updateLinkVisibility = (availableCopies) => {
    const checkoutLink = document.getElementById('checkoutLink');
    const removeCopyLink = document.getElementById('removeCopyLink');

    if (availableCopies > 0) {
        checkoutLink.style.display = 'inline';
        removeCopyLink.style.display = 'inline';
    } else {
        checkoutLink.style.display = 'none';
        removeCopyLink.style.display = 'none';
    }
}

const fetchCurrentHolders = (bookId) => {
    console.log("Fetching current holders for bookID:", bookId);

    // fetch data from checkout_register table for given bookID
    fetch(`http://localhost:8080/api/registers/book/${bookId}`)
        .then(response => {
            if (!response.ok) {
                alert("Failed to fetch current holders!");
                throw new Error("Failed to fetch current holders!");
            }
            return response.json();
        })
        .then(registers => {
            console.log("registers: ", registers);
            // filter these registers to include only with returnDate === null
            const currentHolders = registers.filter(register => register.returnDate === null);

            // user member ids from the filtered registers to fetch member details
            const memberDetailsPromises = currentHolders.map(register =>
                fetch(`http://localhost:8080/api/members/${register.memberId}`)
                    .then(response => {
                        if (!response.ok) {
                            alert("Failed to fetch member details");
                            throw new Error("Failed to fetch member details")
                        }
                        return response.json();
                    })
                    .then(member => ({
                        ...member,
                        checkoutDate: register.checkoutDate,
                        dueDate: register.dueDate
                    }))
            );
            return Promise.all(memberDetailsPromises);
            
        })
        .then(membersWithDetails => {
            displayCurrentHolders(membersWithDetails);
        })
        .catch(error => { 
            alert("Unexpected error while fetching current holders");
            console.error("Unexpected error while fetching current holders", error);
        });

}

const displayCurrentHolders = (members) => {
    console.log("Current holders to display: ", members);

    let html = '<p>No current holders</p>';

    if (members.length > 0) {
        html = `
        <h2>Current Holders</h2>
        <table>
            <thead>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Card #</th>
                    <th>Checkout Date</th>
                    <th>Due Date</th>
                </tr>
            <thead>
            <tbody>
        `;

        members.forEach(member => { 
            const checkoutDate = new Date(member.checkoutDate).toLocaleDateString();
            const dueDate = new Date(member.dueDate).toLocaleDateString();

            html += `<tr>
                        <td>${member.firstName}</td>
                        <td>${member.lastName}</td>
                        <td>${member.id}</td>
                        <td>${checkoutDate}</td>
                        <td>${dueDate}</td>
                    </tr>
                    `;
        });
        if (members.length > 0) {
            html += `</tbody></table>`;
        }

    }
    const currentHoldersContainer = document.getElementById('currentHoldersContainer');
    currentHoldersContainer.innerHTML = html;
}