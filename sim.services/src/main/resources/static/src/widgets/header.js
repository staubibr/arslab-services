'use strict';

import Lang from '../utils/lang.js';

import Widget from '../ui/widget.js';

export default Lang.Templatable("Widget.Header", class Header extends Widget { 

	constructor(id) {
		super(id);
	}
	
	Template() {
		return	"<h1 >RISE Replica</h1>" ;
	}
});