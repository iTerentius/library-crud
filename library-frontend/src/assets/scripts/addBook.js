document.addEventListener('DOMContentLoaded', () => { 
    const form = document.querySelector('form');
    form.addEventListener('submit', function (ev) { 
        ev.preventDefault();
        const formData = {
            title: document.getElementById("title").value,
            author: document.getElementById("author").value,
            isbn: document.getElementById("isbn").value,
            publisher: document.getElementById("publisher").value,
            yearOfPublication: document.getElementById("yearPublished").value,
            placeOfPublication: document.getElementById("placePublished").value,
            noOfAvailableCopies: document.getElementById("noOfAvailableCopies").value,
            barcodeNumber: document.getElementById("barcodeNumber").value
        }

        addNewBook(formData, form);
    });
});

function addNewBook(data, form) {
    fetch('http://localhost:8080/api/books/addBook', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    }) // API Contract
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to add new book');
            }
            return response.json();
        })
        .then(data => {
            console.log("Book added succesfully!");
            alert("New book added successfully!");
            form.reset();
        })
        .catch(error => {
            console.error('Error', error);
            alert("An error occurred.");
        });
}