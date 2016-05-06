// JavaScript Document
	function skin_hover(event){
		var box = this;
		box.oldclass=box.className;
		box.className="_dct_skin_hover";
	}
	function skin_out(event){
		var box = this;
		box.className=box.oldclass;
	}
	function getBox(event){
		if (navigator.userAgent.indexOf('MSIE')!=-1){
			return document.srcelement;
		}else
			return event.currentTarget;
	}
	function SkinBox(table){
		var table=document.getElementById(table);
		for (var i=0;i<table.rows.length;i++){
			var row = table.rows[i];
			for (var j=0;j<row.cells.length;j++){
				row.cells[j].onmouseover=skin_hover;
				row.cells[j].onmouseout=skin_out;
			}
		}
	}
