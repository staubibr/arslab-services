'use strict';
import Sim from '../utils/sim.js';
import Lang from '../utils/lang.js';
import Array from '../utils/array.js';
import Widget from '../ui/widget.js';
import Dropzone from './dropzone.js';

import Net from '../utils/net.js';

export default Lang.Templatable("Widget.Control", class Control extends Widget { 

	
	get Config() { return this.config; }
	
	constructor(node) {		
		super(node);
		
		this.files = null;
		this.config = null;
	

		this.Node("sendFiles").addEventListener("click", this.onSendFiles_Handler.bind(this));
		this.Node("dropzone").On("Change", this.onDropzoneChange_Handler.bind(this));
		
		
	}



	onSendFiles_Handler(ev) {
		console.log(this.files);
	}

	onDropzoneChange_Handler(ev) {
		this.files = ev.files;


		
	}
	
	
	
	onError_Handler(ev) {

		this.Node("dropzone").Reset();
		
		alert(ev.error.toString());
	}
	



	Template() {
		return "<div class='control row'>" +
				  "<div class='file-upload'>" +
				   
					 
						 "<div handle='dropzone' class='dropzone' widget='Widget.Dropzone'></div>" +
						 "<button handle='sendFiles' class='file-save-btn' >Send files</button>" +
						  "</div>" +
					  
					  "<div >" ;
	}
});