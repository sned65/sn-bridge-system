
/*
 * This function is used to show (and hide) HTML content (accordion-like behavior).
 * 
 * idInfo - id of an element like <div id="Demo1" class="w3-container w3-hide"> to show/hide.
 * idRight - id of a directional element used when additional content is hidden.
 * idDown - id of a directional element used when additional content is shown.
 */
function toggleDetails(idInfo, idRight, idDown) {
    var x = document.getElementById(idInfo);
    var right = document.getElementById(idRight);
    var down = document.getElementById(idDown);
    console.log("toggleDetails() "+x);
    console.log("toggleDetails() "+right);
    console.log("toggleDetails() "+down);
    toggleElem(x);
    toggleElem(right);
    toggleElem(down);
    /*
    if (x.className.indexOf("w3-show") == -1) {
        x.className += " w3-show";
    } else { 
        x.className = x.className.replace(" w3-show", "");
    }
    */
}

function toggleElem(elem) {
    if (elem.className.indexOf("w3-show") == -1) {
        elem.className += " w3-show";
    } else { 
        elem.className = elem.className.replace(" w3-show", "");
    }
}


/*
function myAccFunc() {
    var x = document.getElementById("demoAcc");
    if (x.className.indexOf("w3-show") == -1) {
        x.className += " w3-show";
        x.previousElementSibling.className += " w3-green";
    } else { 
        x.className = x.className.replace(" w3-show", "");
        x.previousElementSibling.className = 
        x.previousElementSibling.className.replace(" w3-green", "");
    }
}
*/

// Shows the modal confirmation dialog with id = 'confirmRemoving'.
// rmUrl - REST URL on which the 'DELETE' verb will be sent.
//         Will be stored into the element with id = 'rm_sys_url'.
// msg - a message to be shown in the dialog window.
	function showConfirmationDialog(rmUrl, msg) {
		console.log("removeConfirmation() "+rmUrl+": "+msg);
		//console.log("removeConfirmation() "+document.getElementById('confirmRemoving'));
		//console.log("removeConfirmation() "+document.getElementById('rm_sys_url'));
		//console.log("removeConfirmation() "+document.getElementById('confirm_message'));
		document.getElementById('confirmRemoving').style.display='block';
		document.getElementById('rm_sys_url').value = rmUrl;
		document.getElementById('confirm_message').innerHTML = msg;
		return false;
	}

	// Hides the modal confirmation dialog with id = 'confirmRemoving'.
	function hideConfirmationDialog() {
		document.getElementById('confirmRemoving').style.display='none';
		//console.log("hideConfirmationDialog() "+document.getElementById('confirmRemoving'));
		//console.log("hideConfirmationDialog() "+document.getElementById('rm_sys_url'));
		//console.log("hideConfirmationDialog() "+document.getElementById('confirm_message'));
	}

	// Sends the 'DELETE' verb to the URL extracted from the element with id = 'rm_sys_url'.
	function remove() {
		var rmUrl = document.getElementById('rm_sys_url');
		hideConfirmationDialog();
		//alert("Delete "+rmUrl.value);
		var client = new XMLHttpRequest();
		client.onload = function handler() {
			if (this.status == 200) {
				// success!
				location.reload(true);
			} else {
				// something went wrong
				alert("Status = "+this.status)
			}
		}
		client.open("DELETE", rmUrl.value);
		client.send();
	}
