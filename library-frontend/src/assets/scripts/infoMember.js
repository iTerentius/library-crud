let memberId = null;

document.addEventListener("DOMContentLoaded", () => {
  memberId = getQueryParam("id");
  if (!memberId) {
    console.error("No member ID specified");
    return;
  }

  const url = `http://localhost:8080/api/members/${memberId}`;

  fetch(url)
    .then((response) => {
      if (!response.ok) {
        console.log("Response no OK!");
        throw new Error("Response not OK!");
      }
      return response.json();
    })
    .then((data) => {
      displayMemberDetails(data);
      fetchCheckedOutBooks(data.id);
    })
    .catch((error) => {
      console.error("An error ocurred", error);
      alert("An error ocurred");
    });

    const memberHistoryLink = document.querySelector('a[href="member-history.html"]');
    if(memberHistoryLink) memberHistoryLink.href = `member-history.html?id=${memberId}`;

    const editMemberLink = document.querySelector('a[href="edit-member.html"]');
    if (editMemberLink) editMemberLink.href = `edit-member.html?id=${memberId}`;
});

const getQueryParam = (param) => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(param);
};

const displayMemberDetails = (member) => {
  document.getElementById(
    "member-full-name"
  ).textContent = `${member.firstName} ${member.lastName}`;
  document.getElementById("cardNumber").textContent = member.id;

  let address = member.address
    ? ` ${member.address.streetNumber}
                    ${member.address.streetName},
                    ${member.address.zipCode},
                    ${member.address.placeName},
                    ${member.address.country}
                    ${member.address.additionalInfo || ""}`
    : "N/A";
  document.getElementById("address").textContent = address;
  document.getElementById("phone").textContent = member.phone;
  document.getElementById("email").textContent = member.email;
  document.getElementById("dob").textContent = member.dateOfBirth;
  document.getElementById("membershipStarted").textContent =
    member.membershipStarted;
  document.getElementById("membershipEnded").textContent =
    member.membershipEnded ? member.membershipEnded : "--";
  document.getElementById("membershipStatus").textContent = member.isActive
    ? "Active"
      : "Inactive";
    
    const membershipActionLink = document.getElementById("membershipActionLink");
    if (member.isActive) {
        membershipActionLink.textContent = "Terminate Membership";
        membershipActionLink.href = 'javascript:terminateMembership()';
    } else {
        membershipActionLink.textContent = "Activate Membership";
        membershipActionLink.href = 'javascript:activateMembership()';
    }

    const checkedOutBooksLink = document.getElementById('checkoutLink');
    if (member.isActive) {
        checkedOutBooksLink.style.display = 'inline';
        checkedOutBooksLink.href = `book-checkout.html?memberBarcode=${member.barcodeNumber}`;
    } else {
        checkedOutBooksLink.style.display = 'none';
    }
};

const fetchCheckedOutBooks = (memberId) => {
  const url = `http://localhost:8080/api/registers/member/${memberId}`;

    fetch(url)
        .then((response) => {
            if (!response.ok) {
                console.error("Response no OK!", error);
                throw new Error("Response not OK!");
            }
            return response.json();
        })
        .then((data) => {
            if (data.length === 0) {
                displayCheckedOutBooks([]);
                return;
            }
            // filter out books to display
            const bookDetailsPromise = data.filter((data) => data.returnDate === null)
                .map((data) =>
                    fetch(`http:localhost:8080/api/books/${data.bookId}`)
                        .then((response) => {
                            if (!response.ok) {
                                console.error("Response not OK!", error);
                                throw Error("Response not OK!");
                            }
                            return response.json();
                        })
                        .then((bookDetails) => ({
                            ...bookDetails,
                            dueDate: data.dueDate,
                            registerId: data.id,
                        }))
                        .catch((error) => {
                            console.error("An error occurred", error);
                            alert.apply("An error occurred.");
                        })
                );
            Promise.all(bookDetailsPromise)
                .then((bookDetails) => {
                    displayCheckedOutBooks(bookDetails);
                })
        })
        .catch(error => {
            console.error('An error ocurred', error);
            alert('An error ocurred');
        });
};

const displayCheckedOutBooks = (books) => {

    console.log("Books to display: ", books);

    const booksHeading = document.getElementById('checkedOutBooksHeading');
    const booksTable = document.getElementById('checkedOutBooks');

    booksTable.innerHTML = '';

    if (books.length === 0 || !books) {
        booksHeading.style.display = 'none';
        return;
    }

    let tbody = document.createElement('tbody');
    booksTable.appendChild(tbody);

    books.forEach((book, index) => { 
        let row = tbody.insertRow();
        let detailCell = row.insertCell(0);
        let actionCell = row.insertCell(1);

        detailCell.innerHTML = `${index + 1}. ${book.title}, ${book.author} (Due date: ${book.dueDate})`;
        let returnLink = document.createElement('a');
        returnLink.href = 'javascript:void(0)';
        returnLink.textContent = 'Return book';
        returnLink.onclick = () => {
            returnBook(book.registerId, book.title, book.author);
        }
        actionCell.appendChild(returnLink);
    });
};

const returnBook = (registerId, bookTitle, bookAuthor) => {
    console.log('Inside return() method...');

    const payload = {
        returnDate: new Date().toISOString().split('T')[0]
    };

    fetch(`http://localhost:8080/api/registers/updateRegister/${registerId}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',    
        },
        body: JSON.stringify(payload),
    })
        .then(response => {
            if (!response.ok) {
                console.error('Response not OK!', error);
                throw new Error('Response not OK');
            }
            return response.json()
        })
        .then(async () => {
            const response = await fetch(`http://localhost:8080/api/registers/${registerId}`);
            if (!response.ok) {
                console.error('Failed to fetch updated register details.', error);
                alert("Failed to fetch updated register details.");
                throw new Error('Failed to fetch updated register details.');
            }
            return response.json();
        })
        .then(data => {
            increaseBookCopies(data.bookId);
            return data;
        })
        .then(data => { 
            let alertMessage = `Book "${bookTitle}, ${bookAuthor}" successfully returned.`;
            if (data.overdueFine !== null) {
                let formattedFine = parseFloat(data.overdueFine).toFixed(2);
                alertMessage += `\n\nOverdue Fine: $${formattedFine} USD.`;
            }
            alert(alertMessage);
            fetchCheckedOutBooks(memberId);
        })
        .catch(error => {
            console.error('An error occurred:', error);
            alert('An error occurred.');
    })
    return;
}

const increaseBookCopies = (bookId) => {
    console.log('Inside increased book copies method');
    fetch(`http://localhost:8080/api/books/${bookId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch book details.');
            }
            return response.json();
        })
        .then(data => {
            const updatedCopies = data.noOfAvailableCopies + 1;
            return fetch(`http://localhost:8080/api/books/updateBook/${bookId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ noOfAvailableCopies: updatedCopies })
            });
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update number of book copies');
            }
            return response.json();
        })
        .catch(error => {
            console.error('An error occurred:', error);
            alert('An error occurred.');
        });
}

const terminateMembership = () => {
    updateMembershipStatus(false);
}
const activateMembership = (memberId) => {
    updateMembershipStatus(true);
}

const updateMembershipStatus = (isActive) => {
    // prepare payload
    const today = new Date().toISOString().split('T')[0];
    const payload = isActive ? { membershipEnded: "" } : { membershipEnded: today };
    // use fetch to perform update request
    fetch(`http://localhost:8080/api/members/updateMember/${memberId}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update member status.');
            }
            return response.json();
        })
        .then(data => {
            console.log("Membership status updated successfully.");
            location.reload();
        })
        .catch(error => {
            console.error("Error occurred while trying to update status", error);
            alert("Error occurred while trying to update status");
        });
}