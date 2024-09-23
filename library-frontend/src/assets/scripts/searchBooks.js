document.addEventListener('DOMContentLoaded', () => { 

    const form = document.querySelector('form');

    form.addEventListener('submit', (ev) => { 
        ev.preventDefault();
        const barcodeNumber = document.getElementById('barcodeNumber').value;
        const isbn = document.getElementById('isbnNumber').value;
        const author = document.getElementById('author').value;
        const title = document.getElementById('title').value;
        
        const url = `http://localhost:8080/api/books/search?
                    barcodeNumber=${encodeURIComponent(barcodeNumber)}&
                    isbn=${encodeURIComponent(isbn)}&
                    author=${encodeURIComponent(author)}&
                    title=${encodeURIComponent(title)}`;
        fetch(url)
            .then(response => {
                if (!response.ok) {
                    alert('Network response was not ok!');
                    throw new Error('Network response was not ok!');
                }
                return response.json();
            })
            .then(data => {
                console.log("Search result", data);
                displayResults(data);
            })
            .catch(error => { 
                console.error('Error fetching data', error);
                alert("An error occurred.");
            })
    });
});

const displayResults = (data) => { 
    let html = `<table>
                    <tr>
                        <th>Book ID</th>
                        <th>Title</th>
                        <th>Author</th>
                        <th>Number of Available Copies</th>
                        <th>Action</th>
                    </tr>`;

    data.forEach((book) => { 
        html += `<tr>
                    <td>${book.id}</td>
                    <td>${book.title}</td>
                    <td>${book.author}</td>
                    <td>${book.noOfAvailableCopies}</td>
                    <td><a href="book-info.html?id=${book.id}">View Details</a></td>
                </tr>`;
        
            });
            html += '</table>';
    
            const resultsContainer = document.getElementById('resultsContainer');
    
            resultsContainer.innerHTML = html;
};