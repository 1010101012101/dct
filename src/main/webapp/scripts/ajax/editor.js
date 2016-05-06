/*add by diyanliang 10-3-1 提交DE用*/
function submitDEForm(obj) {
		var strction=obj.name;
		var domstr=FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom');
		document.getElementById('fixDomStr').value=domstr;	
		document.editform.action.value=strction;
		document.editform.submit();
}

/*add by diyanliang 10-3-1 页面预览用*/
function submitPreviewForm(obj) {
	var strction=obj.name;
	document.previewForm.action.value=strction;
	document.previewForm.submit();
}
/*add by diyanliang 10-3-5 重构页面保存按钮触发事件”*/
function saveDEeditor(){
	/*var strhtml=""
	strhtml=FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom');
	strhtml=strhtml.toUpperCase();
	if(strhtml.indexOf("</FORM>")!=-1){
		//var waring='dmlform.savewaring'.localize();
		//Ext.MessageBox.alert('window.info'.localize(),waring)
		return false;
	}*/
	if (Ext.get('section').getValue().length==0){
		Ext.Ajax.request({   
			url: '?a=save&cmd=save',
			params: { 
					htmlPageText: FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom'),
					ResourceId:  Ext.get('ResourceId').getValue(),
					pageTitle: Ext.get('title').getValue(),
					lockVersion:Ext.get('lockVersion').getValue()	
			},							
			failure:function(response, option){
				
			},                                      
			success:function(response, option){
				var resp=response.responseText.split("|")
				document.getElementById('lockVersion').value=resp[1]
				alert(resp[0])
			}                                      
		});
	}
	else {
		Ext.Ajax.request( 
		{   
			waitMsg: 'Save wiki page...',
			url: 'savePage.do?cmd=save&save=savepage',
			params: { 
					//htmlPageText: encodeURI(Ext.get('htmlPageText').getValue()), 
					htmlPageText: FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom'),
					page: Ext.get('page').getValue(),
					spamname: Ext.get('spamname').getValue(),
					spamhash: Ext.get('spamhash').getValue(),
					pageTitle: Ext.get('pageTitle').getValue()
			},							
										
			failure:function(response, option){
					if ((response) && (response.responseText) && (response.responseText.length > 0))
						Ext.MessageBox.alert('window.warning'.localize(), response.responseText);							
			},                                      
			success:function(response, option){
					var msgs = response.responseText.split(";");
					//add by diyanliang,fix bug  popup message jsbug " msgs is not defined" when page loaded 11-21						if(msgs)
					if(msgs)//end
						if (msgs.length == 2) {
							if (msgs[0].length > 0)
								Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
							$('spamhash').value = msgs[1];
						}
						else {
							if (msgs[0].length > 0)
								Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
						}
			}                                      
		});
	}	
	
}

/*add  by diyanliang  10-3-9 重构页面自动保存”*/
//自动保存时间 /min
var autosaveInterval = 5;
//是否提示用户恢复数据0是不提示
var restoreType=0;
var later;
var TempPageData="";
function runAutoSavePage(){
	var AutoSaver = window.setInterval("autoSaveDEeditor()", autosaveInterval*60*1000);
	if(restoreType==1){
		later=window.setInterval("doRestore()",500)
	}
}
//执行备份提示
function doRestore() {
	if(document.readyState=="complete"){
		var editor = FCKeditorAPI.GetInstance('htmlPageText');
		if (editor.EditingArea){//等待编辑区域初始化完成
			var com=confirm('plain.overwrite.changed'.localize(TempPageData));
			if(com){
				strTempPage=document.getElementById('inTempPage').value;
				editor.SetHTML(strTempPage);
			}else{
			}
			window.clearInterval(later);
		}
	}else {
		return;
	}
}

/*add  by diyanliang  10-3-9 重构页面自动保存”*/
function autoSaveDEeditor(){
	/*var strhtml=""
	strhtml=FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom');
	strhtml=strhtml.toUpperCase();
	if(strhtml.indexOf("</FORM>")!=-1){
		return false;
	}*/
	if (Ext.get('section').getValue().length==0){
		Ext.Ajax.request({   
			url: '?a=save&cmd=autosave',
			params: { 
					htmlPageText: FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom'),
					ResourceId:  Ext.get('ResourceId').getValue(),
					pageTitle: Ext.get('title').getValue()				
			},							
			failure:function(response, option){
				
			},                                      
			success:function(response, option){
				var myDate = new Date();
				var mytime=myDate.toLocaleTimeString();
				document.getElementById("infoarea").innerHTML=response.responseText+mytime
			}                                      
		});
	}
	else {
		Ext.Ajax.request( 
		{   
			waitMsg: 'Save wiki page...',
			url: 'savePage.do?cmd=save&save=savepage',
			params: { 
					//htmlPageText: encodeURI(Ext.get('htmlPageText').getValue()), 
					htmlPageText: FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom'),
					page: Ext.get('page').getValue(),
					spamname: Ext.get('spamname').getValue(),
					spamhash: Ext.get('spamhash').getValue(),
					pageTitle: Ext.get('pageTitle').getValue()
			},							
										
			failure:function(response, option){
					if ((response) && (response.responseText) && (response.responseText.length > 0))
						Ext.MessageBox.alert('window.warning'.localize(), response.responseText);							
			},                                      
			success:function(response, option){
					var msgs = response.responseText.split(";");
					//add by diyanliang,fix bug  popup message jsbug " msgs is not defined" when page loaded 11-21						if(msgs)
					if(msgs)//end
						if (msgs.length == 2) {
							if (msgs[0].length > 0)
								Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
							$('spamhash').value = msgs[1];
						}
						else {
							if (msgs[0].length > 0)
								Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
						}
			}                                      
		});
	}	
	
}



//页面锁显示区域处理
function fLocker(){
	var lockType=document.getElementById("lockType").value;
	if(lockType=='locked'){
		document.getElementById("locker").style.display="block";
	}
}

/*
function changeTemplate(obj) {
	if (confirm("plain.confirm".localize())) {
		location.href = obj.value;
	}
}
function makeArray(n) {
	this.length = n;
	for (var i = 0; i < n; i++) {
		this[i] = 0;
	}
	return this;
}
function checkVO() {
	if (document.editform.checkedValue.value.length != 0) {
		window.open("templates/default/clb/voTree.jsp?value=" + document.editform.checkedValue.value, "add", "scrollbars=yes,resizable=yes,top=200,left=600,width=400,height=460");
	} else {
		window.open("templates/default/clb/voTree.jsp", "add", "scrollbars=yes,resizable=yes,top=200,left=600,width=400,height=460");
	}
}
function delPriv() {
	var in_group = document.editform.selectPriv;
	var selected = 0;
	for (var i = 0; i < in_group.length; i++) {
		if (in_group.options[i].selected == 1) {
			selected++;
		}
	}
	if (selected < 1) {
		alert("version.right".localize());
		document.editform.selectPriv.focus();
		return false;
	}	 	
	 	
		//Traverse the inGrp and count all selected items.
	var toRemoveCount = 0;
	var i, j;
	for (var i = 0; i < in_group.length; i++) {
		if (in_group.options[i].selected == 1) {
			toRemoveCount++;
		}
	}
	
		//Create an array for the items that remain & remove.
	var arrayIdx = 0;
	var toRemainArray = new makeArray(in_group.length - toRemoveCount);
	var toRemainArrayValue = new makeArray(in_group.length - toRemoveCount);
	j = 0;
	for (var i = 0; i < in_group.length; i++) {
		if (in_group.options[i].selected == 0) {
			toRemainArray[arrayIdx] = in_group.options[i].text;
			toRemainArrayValue[arrayIdx] = in_group.options[i].value;
			arrayIdx++;
		}
	}

		//Resize the list and rename its items according to the Remain array.
	in_group.length = arrayIdx;
	for (var i = 0; i < toRemainArray.length; i++) {
		in_group.options[i].text = toRemainArray[i];
		in_group.options[i].value = toRemainArrayValue[i];
	}

		// Mark that this is the MoveOut button.
	inGrpItems = in_group.length;
}
function delAllPriv() {
	var in_group = document.editform.selectPriv;
	in_group.length = 0;
}
function addPriv() {
	var operation = document.editform.operation;
	var selected = 0;
	for (var i = 0; i < operation.length; i++) {
		if (operation.options[i].selected == 1) {
			selected++;
		}
	}
	if (selected < 1) {
		alert("plain.choose.operation".localize());
		document.editform.operation.focus();
		return false;
	}
	if (document.editform.checkedValue.value.length == 0) {
		alert("plain.choose.grant".localize());
		document.editform.checkedValue.focus();
		return false;
	}
	var in_group = document.editform.selectPriv;
	var selVO = document.editform.checkedValue.value;
		
		//Create an array and store the selected outGroup names in it.
	var selectVOs = selVO.split(",");
	var listVO = new Array();
	var count = 0;
	for (var i = 0; i < selectVOs.length; i++) {
		for (var j = 0; j < operation.length; j++) {
			if (operation.options[j].selected == 1) {
				listVO[count++] = operation.options[j].value + " " + selectVOs[i];
			}
		}
	}		

		//Add the array elements to the inGroup, only if not already there.
	var j;
	var memberName;
	for (var i = 0; i < listVO.length; i++) {
		memberName = listVO[i];
		for (j = 0; j < in_group.length; j++) {
			if (in_group.options[j].value == memberName) {
				break;
			}
		}

			// The member is not alreay in the group, so add it.
		if (j == in_group.length) {
			in_group.length = j + 1;
			in_group.options[j].text = memberName;
			in_group.options[j].value = memberName;
		}
	}

		// Mark that this is the MoveIn button.
	inGrpItems = in_group.length;
}

function altEditor(original) {
	var editor = document.editform.editor;
	if (original == "plain") {
		location.href = editor.value + "&alteditor=on";
	} else {
		if (confirm("edit.areyousure".localize())) {
			location.href = editor.value + "&alteditor=on";
		} else {
			for (i = 0; i < editor.length; i++) {
				var selected = editor.options[i].value;
				selected = selected.substring(selected.indexOf("editor=") + 7);
				if (selected.indexOf("&") != -1) {
					selected = selected.substring(0, selected.indexOf("&"));
				}
				if (selected == original) {
					editor.options[i].selected = true;
					break;
				}
			}
		}
	}
}
function clickEdit() {
	document.getElementById("searchdiv").style.visibility = "visible";
	document.getElementById("templatediv").style.visibility = "visible";
	TabbedSection.onclick("editPageTag");
}
function clickPara() {
	document.getElementById("searchdiv").style.visibility = "hidden";
	document.getElementById("templatediv").style.visibility = "hidden";
	TabbedSection.onclick("pageParaTag");
}
*/
/*
Ext.onReady(function(){
	new Ext.Panel({
    	contentEl: 'grant-area',
    	width: 400,
    	title: 'Grant',
    	hideCollapseTool: true,
    	titleCollapse: true,
    	collapsible: true,
    	collapsed: true,
    	renderTo: 'grant-panel'
    });
});*/
//Ext.onReady(function () {
//	var win;
//	var button = Ext.get('selectVO');
	
//	button.on('click', function(){
//	if (!win) {
//		win = new Ext.Window(
//		{title:"VOTree", 
//		layout:"fit", 
//		width:500, 
//		height:300, 
//		closeAction:"close", 
//		modal:true, 
//		autoLoad:{url:"templates/default/clb/voTree.jsp", callback:function () {
//		}, scope:this, scripts:true}, buttons:[{text:"Close", handler:function () {
//		win.close();
//		}}]});
//	}
//	win.show(this);
//    });
//});

/*
//If u want to edit this following src, pls contact diyanliang ,because this src is link to others 08-11-20
function saveedit() {
	var strhtml=""
	strhtml=FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom');
	strhtml=strhtml.toUpperCase();
	if(strhtml.indexOf("</FORM>")!=-1){
		var waring='dmlform.savewaring'.localize();
		Ext.MessageBox.alert('window.info'.localize(),waring)
		return false;
	}
	alert("saveedit")
	if (!Ext.get('editorarea')) {
		if (Ext.get('section').getValue().length==0) {
			Ext.Ajax.request( 
			{   
				waitMsg: 'Save wiki page...',
				url: 'savePage.do?cmd=save&save=savepage',
				params: { 
						htmlPageText: FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom'),
						page: Ext.get('page').getValue(),
						spamname: Ext.get('spamname').getValue(),
						spamhash: Ext.get('spamhash').getValue(),
						pageTitle: Ext.get('pageTitle').getValue()				
				},							
											
				failure:function(response, option){
						if ((response) && (response.responseText) && (response.responseText.length > 0))
							Ext.MessageBox.alert('window.warning'.localize(), response.responseText);							
				},                                      
				success:function(response, option){
						var msgs = response.responseText.split(";");
						//add by diyanliang,fix bug  popup message jsbug " msgs is not defined" when page loaded 11-21						if(msgs)
						if(msgs)//end
							if (msgs.length == 2) {
								if (msgs[0].length > 0)
									Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
								$('spamhash').value = msgs[1];
							}
							else {
								if (msgs[0].length > 0)
									Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
							}
				}                                      
			});
		}
		else {
			Ext.Ajax.request( 
			{   
				waitMsg: 'Save wiki page...',
				url: 'savePage.do?cmd=save&save=savepage',
				params: { 
						//htmlPageText: encodeURI(Ext.get('htmlPageText').getValue()), 
						htmlPageText: FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom'),
						page: Ext.get('page').getValue(),
						spamname: Ext.get('spamname').getValue(),
						spamhash: Ext.get('spamhash').getValue(),
						pageTitle: Ext.get('pageTitle').getValue()
				},							
											
				failure:function(response, option){
						if ((response) && (response.responseText) && (response.responseText.length > 0))
							Ext.MessageBox.alert('window.warning'.localize(), response.responseText);							
				},                                      
				success:function(response, option){
						var msgs = response.responseText.split(";");
						//add by diyanliang,fix bug  popup message jsbug " msgs is not defined" when page loaded 11-21						if(msgs)
						if(msgs)//end
							if (msgs.length == 2) {
								if (msgs[0].length > 0)
									Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
								$('spamhash').value = msgs[1];
							}
							else {
								if (msgs[0].length > 0)
									Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
							}
				}                                      
			});
		}	
	}
	else {
		if (Ext.get('section').getValue().length==0) {
			Ext.Ajax.request( 
			{   
				waitMsg: 'Save wiki page...',
				url: 'savePage.do?cmd=save&save=savepage',
				params: { 				
						page: Ext.get('page').getValue(),
						spamname: Ext.get('spamname').getValue(),
						spamhash: Ext.get('spamhash').getValue(),
						_editedtext: Ext.get('editorarea').getValue(),
						pageTitle: Ext.get('pageTitle').getValue()
				},							
											
				failure:function(response, option){
						if ((response) && (response.responseText) && (response.responseText.length > 0))
							Ext.MessageBox.alert('window.warning'.localize(), response.responseText);							
				},                                      
				success:function(response, option){
						var msgs = response.responseText.split(";");
						//add by diyanliang,fix bug  popup message jsbug " msgs is not defined" when page loaded 11-21						if(msgs)
						if(msgs)//end
							if (msgs.length == 2) {
								if (msgs[0].length > 0)
									Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
								$('spamhash').value = msgs[1];
							}
							else {
								if (msgs[0].length > 0)
									Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
							}
				}                                      
			});
		}
		else {
			Ext.Ajax.request( 
			{   
				waitMsg: 'Save wiki page...',
				url: 'savePage.do?cmd=save&save=savepage',
				params: { 				
						page: Ext.get('page').getValue(),
						spamname: Ext.get('spamname').getValue(),
						spamhash: Ext.get('spamhash').getValue(),
						_editedtext: Ext.get('editorarea').getValue(),
						pageTitle: Ext.get('pageTitle').getValue()
				},							
											
				failure:function(response, option){
						if ((response) && (response.responseText) && (response.responseText.length > 0))
							Ext.MessageBox.alert('window.warning'.localize(), response.responseText);							
				},                                      
				success:function(response, option){
						var msgs = response.responseText.split(";");
						//add by diyanliang,fix bug  popup message jsbug " msgs is not defined" when page loaded 11-21						if(msgs)
						if(msgs)//end
							if (msgs.length == 2) {
								if (msgs[0].length > 0)
									Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
								$('spamhash').value = msgs[1];
							}
							else {
								if (msgs[0].length > 0)
									Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
							}
				}                                      
			});
		}
	}
}

function autoSavePage() {
	alert("开始自动保存")
	return false;
	//add by diyanliang	编辑内容中有form 使自动保存时效
	var strhtml=""
		strhtml=FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom');
		strhtml=strhtml.toUpperCase();
		if(strhtml.indexOf("</FORM>")!=-1){
			return false;
		}
	//end
	
	
	
	if (!Ext.get('editorarea')) {
		if (Ext.get('section').getValue().length==0) {
			Ext.Ajax.request( 
			{   
				waitMsg: 'Save temp wiki page...',
				url: 'savePage.do?cmd=tempsave',
				params: { 
						htmlPageText: FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom'),
						spamname: Ext.get('spamname').getValue(),
						spamhash: Ext.get('spamhash').getValue(),
						page: Ext.get('page').getValue()				
				},							
											
				failure:function(response, option){
						if (need_notify_save == 1) {
								if ((response) && (response.responseText) && (response.responseText.length > 0)) {
			   						Ext.MessageBox.alert('window.warning'.localize(), response.responseText);
			   						need_notify_save = 0;
			   					}
			   			}	
				},                                      
				success:function(response, option){
					var myDate = new Date();
					var mytime=myDate.toLocaleTimeString();
					var pageresponseText=response.responseText;
					if(pageresponseText=='AutoSavePageSuc')
						document.getElementById("infoarea").innerHTML="该页面已于"+mytime+"自动保存。";
						if (need_notify_save == 1) {
			   				var msgs = response.responseText.split(";");
			   				if ((msgs) && (msgs.length > 1) && (msgs[1] == 'conflict')) {
			   					if (msgs[0].length > 0) {
			   						Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
			   						need_notify_save = 0;
			   					}
			   				}
			   			}
				}                                      
			});
		}
		else {
			Ext.Ajax.request( 
			{   
				waitMsg: 'Save temp wiki page...',
				url: 'savePage.do?cmd=tempsave',
				params: { 
						htmlPageText: FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom'),
						spamname: Ext.get('spamname').getValue(),
						spamhash: Ext.get('spamhash').getValue(),
						page: Ext.get('page').getValue()
				},							
											
				failure:function(response, option){
						if (need_notify_save == 1) {
								if ((response) && (response.responseText) && (response.responseText.length > 0)) {
			   						Ext.MessageBox.alert('window.warning'.localize(), response.responseText);
			   						need_notify_save = 0;
			   					}
			   			}						
				},                                      
				success:function(response, option){
				//alert("2|"+response.responseText);
						var myDate = new Date();
						var mytime=myDate.toLocaleTimeString();
						var pageresponseText=response.responseText;
						if(pageresponseText=='AutoSavePageSuc')
							document.getElementById("infoarea").innerHTML="该页面已于"+mytime+"自动保存。";
						if (need_notify_save == 1) {
			   				var msgs = response.responseText.split(";");
			   				if ((msgs) && (msgs.length > 1) && (msgs[1] == 'conflict')) {
			   					if (msgs[0].length > 0) {
			   						Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
			   						need_notify_save = 0;
			   					}
			   				}
			   			}
				}                                      
			});
		}
	}
	else {
		if (Ext.get('section').getValue().length==0) {
			Ext.Ajax.request( 
			{   
				waitMsg: 'Save temp wiki page...',
				url: 'savePage.do?cmd=tempsave',
				params: { 
						spamname: Ext.get('spamname').getValue(),
						spamhash: Ext.get('spamhash').getValue(),				
						page: Ext.get('page').getValue(),
						_editedtext: Ext.get('editorarea').getValue()
				},							
											
				failure:function(response, option){
						if (need_notify_save == 1) {
								if ((response) && (response.responseText) && (response.responseText.length > 0)) {
			   						Ext.MessageBox.alert('window.warning'.localize(), response.responseText);
			   						need_notify_save = 0;
			   					}
			   			}				
				},                                      
				success:function(response, option){
				//alert("3|"+response.responseText);
						if (need_notify_save == 1) {
			   				var msgs = response.responseText.split(";");
			   				if ((msgs) && (msgs.length > 1) && (msg[1] == 'conflict')) {
			   					if (msgs[0].length > 0) {
			   						Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
			   						need_notify_save = 0;
			   					}
			   				}
			   			}
				}                                      
			});
		}
		else {
			Ext.Ajax.request( 
			{   
				waitMsg: 'Save temp wiki page...',
				url: 'savePage.do?cmd=tempsave',
				params: { 
						spamname: Ext.get('spamname').getValue(),
						spamhash: Ext.get('spamhash').getValue(),				
						page: Ext.get('page').getValue(),
						_editedtext: Ext.get('editorarea').getValue()
				},							
											
				failure:function(response, option){
						if (need_notify_save == 1) {
								if ((response) && (response.responseText) && (response.responseText.length > 0)) {
			   						Ext.MessageBox.alert('window.warning'.localize(), response.responseText);
			   						need_notify_save = 0;
			   					}
			   			}					
				},                                      
				success:function(response, option){
						var myDate = new Date();
						var mytime=myDate.toLocaleTimeString();
						var pageresponseText=response.responseText;
						if(pageresponseText=='AutoSavePageSuc')
							document.getElementById("infoarea").innerHTML="该页面已于"+mytime+"自动保存。";
						if (need_notify_save == 1) {
			   				var msgs = response.responseText.split(";");
			   				if ((msgs) && (msgs.length > 1) && (msgs[1] == 'conflict')) {
			   					if (msgs[0].length > 0) {
			   						Ext.MessageBox.alert('window.info'.localize(), msgs[0]);
			   						need_notify_save = 0;
			   					}
			   				}
			   			}
				}                                      
			});
		}
	}

}

	
var notifyHasSaved = 0;
var hasSavedTime;
var lastSaveTextHtml;
var pageAsHtml;
var parent;
var selectText = "";
var go_changed;
var go_changedplain;
var need_notify_save = 1;

var saveInterval = Wiki.prefs.get("AutoSave");
if (!saveInterval){}
	saveInterval = 5;

function go_changedFCK() {
	if (getOs() == 'MSIE') {
		go_changed = window.setInterval("go_FCK()",500);
	}
	else {
		notifySaveChanged();
	}
}

function go_FCK(){
   if(document.readyState=="complete"){
      window.clearInterval(go_changed);
   }
   else return;
   notifySaveChanged();
}

function go_changedPlain() {
	if (getOs() == 'MSIE') {
		go_changedplain=window.setInterval("go_plain()",500);
	}
	else {
		notifySaveChangedPlain();
	}
}

function go_plain(){
   if(document.readyState=="complete"){
      window.clearInterval(go_changedplain);
   }
   else return;
   
   notifySaveChanged();
}

function notifySaveChanged() {
	var AutoSaver = window.setInterval("autoSavePage()", saveInterval*60*1000);
	selectText = pageAsHtml;
	if (notifyHasSaved == 1) {
   		Ext.MessageBox.confirm('grant.confirm.title'.localize(),
	   		'plain.overwrite.changed'.localize(hasSavedTime),
			function(btn) {
				if(btn == 'yes') {
					selectText = lastSaveTextHtml ;
					FCKeditorAPI.GetInstance('htmlPageText').SetHTML(selectText);
				}
			}		
		);
   }
   else if (notifyHasSaved == 2) {
   		Ext.MessageBox.confirm('grant.confirm.title'.localize(),
	   		'plain.overwrite.nochange'.localize(hasSavedTime),
			function(btn) {
				if(btn == 'yes') {
					selectText = lastSaveTextHtml;
					FCKeditorAPI.GetInstance('htmlPageText').SetHTML(selectText);
				}
			}
		);
   	}
}

function notifySaveChangedPlain() {
	var AutoSaver = window.setInterval("autoSavePage()", saveInterval*60*1000);
	selectText = pageAsHtml;
	if (notifyHasSaved == 1) {
   		Ext.MessageBox.confirm('grant.confirm.title'.localize(),
	   		'plain.overwrite.changed'.localize(hasSavedTime),
			function(btn) {
				if(btn == 'yes') {
					selectText = lastSaveTextHtml ;
					$('editorarea').value = selectText;
				}
			}		
		);
   }
   else if (notifyHasSaved == 2) {
   		Ext.MessageBox.confirm('grant.confirm.title'.localize(),
	   		'plain.overwrite.nochange'.localize(hasSavedTime),
			function(btn) {
				if(btn == 'yes') {
					selectText = lastSaveTextHtml;
					$('editorarea').value = selectText;
				}
			}
		);
   	}
}

function getOs()
{
	var OsObject = "";
	if(navigator.userAgent.indexOf("MSIE")>0) {
		return "MSIE";
	}
	if(isFirefox=navigator.userAgent.indexOf("Firefox")>0){
		return "Firefox";
	}
	if(isSafari=navigator.userAgent.indexOf("Safari")>0) {
		return "Safari";
	}
	if(isCamino=navigator.userAgent.indexOf("Camino")>0){
		return "Camino";
	}
	if(isMozilla=navigator.userAgent.indexOf("Gecko/")>0){
		return "Gecko";
	}
	
	return "Unknown";
} */