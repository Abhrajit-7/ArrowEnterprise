// Sample tree data
document.addEventListener('DOMContentLoaded', function () {
             const username=localStorage.getItem('username');
             const jwtToken=localStorage.getItem('jwtToken');
            //const userId=1;
            fetchAndPopulateTree(username); // Load data when the page is opened
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

const jwtToken=localStorage.getItem('jwtToken');
// Function to fetch tree structure from backend and populate the tree
async function fetchAndPopulateTree(username) {
    try {
        const response = await fetch('http://arrowenterprise.co.in/api/v1/members/' + username + '/showTree',{
            headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
            }

        }); // Adjust the endpoint URL as per your backend
        const treeData = await response.json();

        // Populate the tree structure dynamically
        const treeContainer = document.getElementById('treeContainer');
        const treeHTML = generateTreeHTML(treeData);
        treeContainer.innerHTML = treeHTML;
    } catch (error) {
        console.error('Error fetching tree structure:', error);
    }
}