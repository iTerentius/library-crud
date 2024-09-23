document.addEventListener('DOMContentLoaded', () => { 
    const form = document.querySelector('form');
    form.addEventListener('submit', function (ev) { 
        ev.preventDefault();

        // get today's date in YYYY-MM-DD
        const today = new Date().toISOString().split('T')[0];
        const formData = {
            firstName: document.getElementById("firstName").value,
            lastName: document.getElementById("lastName").value,
            dateOfBirth: document.getElementById("dob").value,
            address: {
                streetName: document.getElementById("streetName").value, 
                streetNumber: document.getElementById("streetNo").value,
                zipCode: document.getElementById("zipCode").value,
                placeName: document.getElementById("placeName").value,
                country: document.getElementById("country").value,
                additionalInfo: document.getElementById("addInfo").value,
            },
            email: document.getElementById("email").value,
            phone: document.getElementById("phone").value,
            barcodeNumber: document.getElementById("barcode").value,
            membershipStarted: today,
            isActive: true
        }

        addNewMember(formData, form);
    });
});

function addNewMember(data, form) {
    fetch('http://localhost:8080/api/members/addMember', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    }) // API Contract
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to add new member');
            }
            return response.json();
        })
        .then(data => {
            console.log("Member added succesfully!");
            alert("New member added successfully!");
            form.reset();
        })
        .catch(error => {
            console.error('Error', error);
            alert("An error occurred.");
        });
}