// Sample tree data
document.addEventListener('DOMContentLoaded', function () {
            fetchAndPopulateTree(1); // Load data when the page is opened
        });
// Function to generate HTML for tree structure
function generateTreeHTML(node, isRoot=true) {
    
    let html=' ';

        if (isRoot) {
    html += '<ul>'; // Start the <ul> tag for the root node
}
    
    
    html += `<li><a href="#"><span>${node.username}</span></a>`;
        //html+='<ul>'

    if (node.children && node.children.length > 0) {
        html += '<ul>';
        node.children.forEach(child => {
            //html += `<li><a href="#"><span>${child.name}</span></a>`;
            html += generateTreeHTML(child, false);
        });
        html += '</ul>';
    }

    html += '</li>';

    if (isRoot ) {
    html += '</ul>'; // Close the <ul> tag for the root node if it has children
}

    return html;
}

// Populate the tree structure using sample data
//const treeContainer = document.getElementById('treeContainer');
//treeContainer.innerHTML = generateTreeHTML(sampleTreeData);


// Function to fetch tree structure from backend and populate the tree
async function fetchAndPopulateTree(userId) {
    try {
        const response = await fetch('http://localhost:8080/api/v1/members/{userId}/showTree'); // Adjust the endpoint URL as per your backend
        const treeData = await response.json();
        //const userId=user.id;

        // Populate the tree structure dynamically
        const treeContainer = document.getElementById('treeContainer');
        const treeHTML = generateTreeHTML(treeData);
        treeContainer.innerHTML = treeHTML;
    } catch (error) {
        console.error('Error fetching tree structure:', error);
    }
}