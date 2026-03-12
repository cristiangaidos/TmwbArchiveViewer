console.log("Communication JS");

function loadToolkit(graphData, helpLinkUrl) {
	console.log("Loading Graph Data");
	console.log(helpLinkUrl);
	console.log(JSON.stringify(graphData));
	toolkit.clear();
	toolkit.load({
		data: JSON.parse(graphData)
	}); // listener for mode change on renderer.
	renderer.zoomToFit();

	document
		.getElementsByClassName("jtkfa jtkfa-help")[0]
		.addEventListener("click", function(event) {
			window.open(helpLinkUrl, "_blank").focus();
		});

	document
		.getElementsByClassName("jtkfa jtkfa-download pi pi-download")[0]
		.addEventListener("click", function(event) {
			renderImage();
		});


}

/** 
	Exports the current graph view as a PNG and download it.
*/
window.renderImage = function renderImage() {

	const graphTarget = document.getElementById('jtk-graphview-flowchart');
	var svgs = Array.prototype.slice.call(graphTarget.getElementsByTagName('svg'));
	
	
	// Replace all SVGs with canvas elements
	svgs.forEach(function(svg) {
		try {
			var svgExample = svg;
			var serializer = new XMLSerializer();
			var svgMarkup = serializer.serializeToString(svgExample);
			if (svgMarkup.indexOf("jtk-connector") > -1) {

				var connectorCanvas = document.createElement('canvas');
				connectorCanvas.className = "screenShotTempCanvas";
				
				//add SVG connector to a canvas
				canvg(connectorCanvas, svgMarkup); 

				addCssStyle(connectorCanvas, svgExample.style)
				
				// hide the SVG as it cannot be exported by html2canvas
				svgExample.classList.add("tempHide");
				
				// Insert canvas containing the connector next to SVG element.
				insertAfter(svgExample, connectorCanvas);

			}
		}
		catch (err) {
			alert('Export Exception: ' + err);
		}
	});

	
	// Convert the graph view div to a canvas element and download it as PNG
	renderer.zoomToFit();
	convertHTML2Canvas();
	
	// Remove the added canvases
	var cnvs = Array.prototype.slice.call(graphTarget.getElementsByClassName('screenShotTempCanvas'));
	cnvs.forEach(function(cnv) {
		cnv.remove();
	});

	// Show the SVG elements again after image export
	svgs = Array.prototype.slice.call(graphTarget.getElementsByTagName('svg'));
	svgs.forEach(function(svg) {
		svg.classList.remove("tempHide");
	});


};

function addCssStyle(element, style) {
	for (const property in style)
		element.style[property] = style[property];
}

function insertAfter(referenceNode, newNode) {
	referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
}

function convertHTML2Canvas() {

	const graphTarget = document.getElementById('jtk-graphview-flowchart');
	const canvasDiv = document.getElementsByClassName('jtk-graphview-canvas graphview-canvas-dotted jtk-draggable katavorio-delegated-draggable jtk-surface')[0];
	
	
	html2canvas(graphTarget, { allowTaint: true, useWidth: canvasDiv.style.width, useHeight: canvasDiv.style.height }).then((canvas) => {

		let newCanv = cloneCanvas(canvas);
		document.body.appendChild(newCanv);

		const canvasDiv = document.getElementsByClassName('jtk-graphview-canvas graphview-canvas-dotted jtk-draggable katavorio-delegated-draggable jtk-surface')[0];
		newCanv.style.width = canvasDiv.getBoundingClientRect().width;
		newCanv.style.height = canvasDiv.getBoundingClientRect().height;

		const baseImg = newCanv.toDataURL("image/png");
		var anchor = document.createElement('a');
		anchor.setAttribute("href", baseImg);
		anchor.setAttribute("download", "RulesetExport");
		anchor.click();
		anchor.remove();
		newCanv.remove();

	});

}


function cloneCanvas(oldCanvas) {

	//create a new canvas
	var newCanvas = document.createElement('canvas');
	var context = newCanvas.getContext('2d');

	//set dimensions
	newCanvas.width = oldCanvas.width;
	newCanvas.height = oldCanvas.height;

	//apply the old canvas to the new one
	context.drawImage(oldCanvas, 0, 0);

	//return the new canvas
	return newCanvas;
}

