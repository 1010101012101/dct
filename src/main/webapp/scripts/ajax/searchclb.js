//searchclb.js created by morrise 20080516
//searchCBL.jsp js source
//
Ext.BLANK_IMAGE_URL = 'scripts/extjs/resources/images/default/s.gif';

function checkValue2() {
    if(document.searchCLBForm.condition.value.trim()==""){
	   Alert("searchclb.enter".localize());
	   document.searchCLBForm.condition.focus();
 	   return false;
	}
	
	document.searchCLBForm.opt.value="3";
	document.searchCLBForm.submit();
}

function CheckAll(){
	for (var i=0;i<document.searchCLBForm.elements.length;i++){
	var e = document.searchCLBForm.elements[i];
	if ((e.name != 'chkAll') && (e.name.indexOf("check")!=-1))
		e.checked = document.searchCLBForm.chkAll.checked;
	}
}

function deleteCLB() {
	hideAll();
	var flag1="false";
	for (var j=0;j<document.searchCLBForm.elements.length;j++){
		var e1 = document.searchCLBForm.elements[j];
		if (e1.checked) {
			flag1="true";
		}
	}
	if (flag1!="true") {
		Alert("searchclb.choose".localize());
		return false;
	}
	var url = location.href;
	url = url.substring(url.indexOf("?")+1);
	var tag, tags;
	var fields = url.split("&");
	for (var i=0; i<fields.length; i++) {
		if (fields[i].indexOf("tagID")!=-1)
			tag = fields[i].substring(fields[i].indexOf("=")+1);
		if (fields[i].indexOf("tags")!=-1)
			tags = fields[i].substring(fields[i].indexOf("=")+1);
	}
	if(confirm("searchclb.confirm".localize())) {
		document.searchCLBForm.opt.value="10";
		document.searchCLBForm.tagID.value=tag;
		document.searchCLBForm.tags.value=tags;
		document.searchCLBForm.submit();
	}
}
function grantCLB() {
	display(document.getElementById("grantArea"));
	var flag2="false";
	for (var k=0;k<document.searchCLBForm.elements.length;k++){
		var e2 = document.searchCLBForm.elements[k];
		if ((e2.name != 'chkAll') && (e2.name.indexOf("check")!=-1))
			if (e2.checked) {
				flag2="true";
			}
	}
	if (flag2 != "true") {
		Alert("searchclb.choose".localize());
		return false;
	}
	if (document.searchCLBForm.checkedValue.value.length != 0)
		window.open('templates/avlab/clb/voTree.jsp?value='+document.searchCLBForm.checkedValue.value,'add','scrollbars=yes,resizable=yes,top=200,left=600,width=400,height=460');
	else
		window.open('templates/avlab/clb/voTree.jsp','add','scrollbars=yes,resizable=yes,top=200,left=600,width=400,height=460');
	//document.searchCLBForm.action="grantCLB.do?opt=1";
	//document.searchCLBForm.submit();
}

function grantOK() {
	var flag = false;
	for (var i=0;i<document.searchCLBForm.elements.length;i++) {
		var e = document.searchCLBForm.elements[i];
		if ((e.name != 'chkAll') && (e.name.indexOf("check")!=-1))
			if (e.checked) {
				flag = "true";
			}
	}
	if (flag != "true") {
		Alert("searchclb.choose".localize());
		return false;
	}
	
	//var checks = document.getElementsByName("operations");
	//var Checked = false;
	//for (i=checks.length-1; i>=0; i--)
	//	if (checks[i].checked == true)
	//		Checked = true;
	
	//if (Checked == false) {
  	//  Alert("searchclb.grant.condition".localize());
	//   document.grantCLBForm.operations.focus();
 	//   return false;
	//}
	
	document.searchCLBForm.opt.value="30";
	document.searchCLBForm.checkedValue.disabled = false;
	document.searchCLBForm.submit();
}

function addPage() {
	//hideAll();
	if (!checkSelected())
		return false;
	else
		return true;
	//display(document.getElementById("addPageArea"));
}


function setAction() {
	if (document.searchCLBForm.action.value != 0) {
		if (!checkSelected()) return false;
	}
	hideAll();
	//if (document.searchCLBForm.action.value == 1)  //Add to page
	//	display(document.getElementById("addPageArea"));
	if (document.searchCLBForm.action.value == 2)  //Add to myresult
   		addMyResult();
	if (document.searchCLBForm.action.value == 3) //grant
   	{	
   		grantCLB();
   	}	 	
	//if (document.searchCLBForm.action.value == 4) //delete document
   	//	deleteCLB();   	
}

function hideAll() {
	//hide(document.getElementById("addPageArea"));
	hide(document.getElementById("grantArea"));
}

function addMyResult() {
	if (!checkSelected()) return false;
	if(confirm("searchclb.myresult".localize())) {
		document.searchCLBForm.opt.value="32";
		document.searchCLBForm.submit();
	}
}

function display(target) {
  	target.style.display="block";
}

function hide(target) {
	target.style.display="none";
}

function checkSelected() {
 	var flag = false;
	for (var i=0;i<document.searchCLBForm.elements.length;i++) {
		var e = document.searchCLBForm.elements[i];
		if ((e.name != 'chkAll') && (e.name.indexOf("check")!=-1))
			if (e.checked) {
				flag = "true";
			}
	}
	if (flag != "true") {
		Alert("searchclb.choose".localize());
		return false;
	}
	return true;
}

function Alert(msg) {
	Ext.Msg.show({
		title	: "window.warning".localize(),
		msg		: msg,
		buttons	: Ext.Msg.OK,
		icon	: Ext.Msg.WARNING
		});
}

Ext.onReady(function(){
    var win;
    var button = Ext.get('addpage');
	if (button) {
		var ds = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({
	            url: 'allPages.do',
				method: 'POST'
	        }),
	        reader: new Ext.data.XmlReader({
	               record: 'page',
	               totalRecords: '@total'
	           	}, [
	          	{name:'name', mapping:'name'},
				'title'
				])
			});
		
	    // Custom rendering Template
	    var resultTpl = new Ext.XTemplate(
	        '<table><tpl for="."><tr class="search-item"><td width="40%">{name}</td><td>{title}</td></tr></tpl></table>'
	    );
	
	   	/*var inputBox = new Ext.form.TextField({
	   		id: 'parentPage',
	   		emptyText: Ext.get('currentParent').value
	   	});
	   	*/
		var search = new Ext.form.ComboBox({
		    store: ds,
		    displayField:'name',
		    typeAhead: true,
		    autoFill:true,
		    loadingText: 'search.hint'.localize(),
		    width: 400,
		    hideTrigger:true,
		    tpl: resultTpl,
		    //applyTo: Ext.get('parentPage'),
		    minChars:1,
		    itemSelector: 'tr.search-item',
		    triggerAction: 'all',
		    emptyText: 'hint.emptycombo'.localize(),
		    selectOnFocus: true
		});
	    
	    button.on('click', function(){
	        // create the window on the first click and reuse on subsequent clicks
	        if (checkSelected()) {
		        if(!win){
		            win = new Ext.Window({
		            	title: 'searchclb.addpage'.localize(),
		                //el: 'addPageArea',
		                layout: 'fit',
		                width: 400,
		                modal: true,
		                height: 100,
		                closeAction: 'hide',
		                plain: true,
		                items: [search],
						
		                buttons: [{
		                    text:'button.submit'.localize(),
		                    handler: function(){
		                    	addCLB();
		                    }
		                },{
		                    text: 'button.cancel'.localize(),
		                    handler: function(){
		                        win.hide();
		                    }
		                }]
		            });
		        }
		        win.show();
	        }
	    });
    }
    
    function addCLB() {
		var flag3="false";
		for (var l=0;l<document.searchCLBForm.elements.length;l++){
			var e3 = document.searchCLBForm.elements[l];
			if (e3.checked) {
				flag3="true";
			}
		}
		if (flag3!="true") {
			Alert("searchclb.choose".localize());
			return false;
		}
		if (search.getValue()=="") {
			Alert("searchclb.target".localize());
			return false;
		}
		
		/*
		Ext.Ajax.request({
   			url: 'docPrivilege.do?cmd=delete',
   			success: sFn(obj),
   			failure: fFn(obj),
   			params: { opt: '9', currentParent: search.getValue(), docid: Ext.get('doc').getValue()}
		});
		
		function sFn(obj) {
			win.hide();
		}
		
		function fFn(obj) {
			win.hide();
			Ext.window.
		}
		*/
		document.searchCLBForm.currentParent.value = search.getValue();
		document.searchCLBForm.opt.value="9";
		document.searchCLBForm.submit();
		
	}     
});  