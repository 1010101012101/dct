<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" >
<!--
 * FCKeditor - The text editor for Internet - http://www.fckeditor.net
 * Copyright (C) 2003-2008 Frederico Caldeira Knabben
 *
 * == BEGIN LICENSE ==
 *
 * Licensed under the terms of any of the following licenses at your
 * choice:
 *
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 *
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 *
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 *
 * == END LICENSE ==
 *
 * Form dialog window.
-->
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="cn.vlabs.duckling.vwb.*"%>
<%@ page import="cn.vlabs.duckling.vwb.ui.command.*" %>
<%
	VWBContext context = VWBContext.createContext(request,VWBCommand.ATTACH,null);
	String basePath = context.getBaseURL()+"/";
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta content="noindex, nofollow" name="robots" />
	<script src="<%=basePath %>scripts/fckeditor/editor/dialog/common/fck_dialog_common.js" type="text/javascript"></script>
	<script type="text/javascript" src="<%=basePath %>scripts/ajax/ajax.js"></script>
	    <link rel="stylesheet" type="text/css" href='<%=basePath %>scripts/extjs/resources/css/ext-all.css' />
	<script type="text/javascript" src='<%=basePath %>scripts/extjs/adapter/ext/ext-base.js'></script>	
	<script type="text/javascript" src='<%=basePath %>scripts/extjs/split/page-ext.js'></script>
	
	<script type="text/javascript">

var dialog	= window.parent ;
var oEditor = dialog.InnerDialogLoaded() ;
var FCKLang	= oEditor.FCKLang ;
// Gets the document DOM
var oDOM = oEditor.FCK.EditorDocument ;
var FCKConfig	= oEditor.FCKConfig ;


var oActiveEl = dialog.Selection.GetSelection().MoveToAncestorNode( 'FORM' ) ;

dialog.AddTab( 'DlgInfoTab', FCKLang.DlgInfoTab ) ;
dialog.AddTab( 'SqlTab',FCKLang.DMLSQLTag ) ;


function OnDialogTabChange( tabCode ){
	ShowE('DlgInfoTab'		, ( tabCode == 'DlgInfoTab' ) ) ;
	ShowE('SqlTab'		, ( tabCode == 'SqlTab' ) ) ;
	dialog.SetAutoSize( true ) ;
}


window.onload = function()
{	
	// First of all, translate the dialog box texts
	oEditor.FCKLanguageManager.TranslatePage(document) ;
	
	if ( oActiveEl )
	{	
		GetE('txtName').value	= oActiveEl.getAttribute( 'dmldesc', 2 ) ;
		GetE('txtName').disabled=true;
		if(oActiveEl.getAttribute('sqlsrc1'))
			GetE('SqlText1').value	= oActiveEl.getAttribute('sqlsrc1') ;
		if(oActiveEl.getAttribute('sqlsrc2'))
			GetE('SqlText2').value	= oActiveEl.getAttribute('sqlsrc2') ;
		if(oActiveEl.getAttribute('sqlsrc3'))
			GetE('SqlText3').value	= oActiveEl.getAttribute('sqlsrc3') ;	
		if(oActiveEl.getAttribute('changepage'))
			GetE('changepage').value	= oActiveEl.getAttribute('changepage') ;		
		
		/*if(oActiveEl.getAttribute('resourceid'))
			GetE('ResourceId').value	= oActiveEl.getAttribute('resourceid') ;		*/
		/*//GetE('txtName').value	= oActiveEl.name ;
		//GetE('txtMethod').value	= oActiveEl.method ;*/
	}
	else
		oActiveEl = null ;
	//SelectField( 'txtName' ) ;
	dialog.SetOkButton( true ) ;
	document.getElementById('DlgInfoTab').style.display = '' ;
	dialog.SetAutoSize( true ) ;
	

}
function callback(){
	if (xmlHttp.readyState == 4) { // 判断对象状
           if (xmlHttp.status == 200) { // 信息已经成功返回，开始处理信
	           	if(xmlHttp.responseText==0){
	           		/*if ( !oActiveEl ){
						oActiveEl = oEditor.FCK.InsertElement( 'form' ) ;
						if ( oEditor.FCKBrowserInfo.IsGeckoLike )
							oEditor.FCKTools.AppendBogusBr( oActiveEl ) ;
					}
					oActiveEl.method ="post"
					oActiveEl.setAttribute('dmldesc',GetE('txtName').value );
					if(GetE('SqlText1').value)
						oActiveEl.setAttribute('sqlsrc1',GetE('SqlText1').value );
					else
						oActiveEl.removeAttribute('sqlsrc1');
					
					if(GetE('SqlText2').value)
						oActiveEl.setAttribute('sqlsrc2',GetE('SqlText2').value );
					else
						oActiveEl.removeAttribute('sqlsrc2');
						
					if(GetE('SqlText3').value)
						oActiveEl.setAttribute('sqlsrc3',GetE('SqlText3').value );
					else
						oActiveEl.removeAttribute('sqlsrc3');	
						
						
					if(GetE('changepage').value)
						oActiveEl.setAttribute('changepage',GetE('changepage').value );
					else
						oActiveEl.removeAttribute('changepage');		
					window.parent.CloseDialog();*/
					createFormElmt();
				}else{
					var type=confirm(FCKLang.DMLCheckFormWarning)
					if(type){
						createFormElmt();
					}
	           		//alert("已经存在同名表单,请改名")
	           	}
           } else { 
                 alert("error request");
           }
	 }

}

function Ok()
{



	if(!GetE('txtName').value){
		alert(FCKLang.DMLFormWarning);
		return;
	}
	
	
	var txtname=GetE('txtName').value
	var endChar=txtname.substring(txtname.length-1,txtname.length);
	if(endChar=='表'){
		alert(FCKLang.DMLFormNameWarning)
		return;
	}


	//安全验证
	if(GetE('SqlText1').value){
		var chstr=GetE('SqlText1').value;
		if(checkSql(chstr)==1){
			alert(FCKLang.DMLSqlWarning);
			return;
		}
		if(checkSql(chstr)==2){
			alert(FCKLang.DMLSqlTableNameWarning);
			return;
		}
	}
	if(GetE('SqlText2').value){
		var chstr=GetE('SqlText2').value;
		if(checkSql(chstr)==1){
			alert(FCKLang.DMLSqlWarning);
			return;
		}
		if(checkSql(chstr)==2){
			alert(FCKLang.DMLSqlTableNameWarning);
			return;
		}
	}
	if(GetE('SqlText3').value){
		var chstr=GetE('SqlText3').value;
		if(checkSql(chstr)==1){
			alert(FCKLang.DMLSqlWarning);
			return;
		}
		if(checkSql(chstr)==2){
			alert(FCKLang.DMLSqlTableNameWarning);
			return;
		}
	}

	if ( !oActiveEl ){
		var strFormid=GetE('txtName').value
		if(strFormid!=""){
			var ajaxurl=FCKConfig.DucklingBaseHref+'DmlFormAction.do?type=find&dmlformid='+strFormid;
			send_request("get", ajaxurl, "", "text", callback); 
		}
	}else{
		oActiveEl.method ="post"
		oActiveEl.setAttribute('dmldesc',GetE('txtName').value );
		if(GetE('SqlText1').value)
			oActiveEl.setAttribute('sqlsrc1',GetE('SqlText1').value );
		else
			oActiveEl.removeAttribute('sqlsrc1');
		
		if(GetE('SqlText2').value)
			oActiveEl.setAttribute('sqlsrc2',GetE('SqlText2').value );
		else
			oActiveEl.removeAttribute('sqlsrc2');
			
		if(GetE('SqlText3').value)
			oActiveEl.setAttribute('sqlsrc3',GetE('SqlText3').value );
		else
			oActiveEl.removeAttribute('sqlsrc3');	
			
		if(GetE('changepage').value){
			oActiveEl.setAttribute('changepage',GetE('changepage').value);
		}else{
			oActiveEl.removeAttribute('changepage');
		}
		
		if(GetE('ResourceId').value&&GetE('changepage').value){
				oActiveEl.setAttribute('resourceid',GetE('ResourceId').value);
		}else{
			oActiveEl.removeAttribute('resourceid');
		}
		
		return true ;
	}
	
	
	/*
	if ( !oActiveEl )ResourceId
	{
		oActiveEl = oEditor.FCK.InsertElement( 'form' ) ;

		if ( oEditor.FCKBrowserInfo.IsGeckoLike )
			oEditor.FCKTools.AppendBogusBr( oActiveEl ) ;
	}

	oActiveEl.method ="post"
	oActiveEl.setAttribute('dmldesc',GetE('txtName').value );
	return true ;*/
}

function  test(){
	alert("test")
	var strFormid=GetE('txtName').value
	if(strFormid!=""){
				var ajaxurl='/dct/DmlFormAction.do?type=find&dmlformid='+strFormid;
     		send_request("get", ajaxurl, "", "text", callback); 
	}
}


function checkSql(str){
	if(str.indexOf("drop")!=-1||str.indexOf("delete")!=-1||str.indexOf("DROP")!=-1||str.indexOf("DELETE")!=-1){
		return 1;
	}else if(str!=""&&str.indexOf("$site$")==-1){
		return 2;
	}
	else{
		return -1
	}
}

function createFormElmt(){

	if ( !oActiveEl ){
		oActiveEl = oEditor.FCK.InsertElement( 'form' ) ;
		if ( oEditor.FCKBrowserInfo.IsGeckoLike )
			oEditor.FCKTools.AppendBogusBr( oActiveEl ) ;
	}
	oActiveEl.method ="post"
	oActiveEl.setAttribute('dmldesc',GetE('txtName').value );
	if(GetE('SqlText1').value)
		oActiveEl.setAttribute('sqlsrc1',GetE('SqlText1').value );
	else
		oActiveEl.removeAttribute('sqlsrc1');
	
	if(GetE('SqlText2').value)
		oActiveEl.setAttribute('sqlsrc2',GetE('SqlText2').value );
	else
		oActiveEl.removeAttribute('sqlsrc2');
		
	if(GetE('SqlText3').value)
		oActiveEl.setAttribute('sqlsrc3',GetE('SqlText3').value );
	else
		oActiveEl.removeAttribute('sqlsrc3');	
		
		
	if(GetE('changepage').value)
		oActiveEl.setAttribute('changepage',GetE('changepage').value );
	else
		oActiveEl.removeAttribute('changepage');		
		
		
	if(GetE('ResourceId').value){
		oActiveEl.setAttribute('resourceid',GetE('ResourceId').value);
	}else{
		oActiveEl.removeAttribute('resourceid');
	}	
		
	window.parent.CloseDialog();
}









//Page标签下自动搜索
Ext.onReady(function(){
	 var ds = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({
            url: FCKConfig.DucklingBaseHref+'allresource.do',
			method: 'POST'
        }),
        reader: new Ext.data.XmlReader({
               record: 'page',
               totalRecords: '@total'
           	}, [
           	    {name:'id', mapping:'id'},
           	    'title','type'
			])
		});

    // Custom rendering Template
    var resultTpl = new Ext.XTemplate(
        '<table><tpl for="."><tr class="search-item" ><td title="{title}"><div style="width:150;overflow:hidden">{title}({id})</div></td><td >{type}</td></tr></tpl></table>'
    );

   	var inputBox = "changepage";
   	if ((inputBox)) {
	   	var search = new Ext.form.ComboBox({
			hiddenName:"ResourceId",
			valueField : 'id',//值
	   		listWidth: 250,
	        store: ds,
	        displayField:'title',
	        typeAhead: true,
	        loadingText: 'loading...',
	        width: 250,
	        hideTrigger:true,
	        tpl: resultTpl,
	        applyTo: inputBox,
	        minChars:1,
	        itemSelector: 'tr.search-item',
	        lazyInit:true,
	        //triggerAction: 'all',
	        selectOnFocus: true
	    });
	    if ( oActiveEl ){
	   	 	if(oActiveEl.getAttribute('resourceid'))
	   	 		GetE('ResourceId').value	= oActiveEl.getAttribute('resourceid') ;
	    }
    }
   	
});
	</script>
</head>
<body class="InnerBody" style="overflow: hidden">
 <div id="DlgInfoTab" align="center" width="100%" style="DISPLAY: none">
	<table width="100%" style="height: 100%">
		<tr>
			<td align="center">
				<table cellspacing="0" cellpadding="0" width="80%" border="0">
					<tr>
						<td>
							<span fcklang="DlgFormName">Name</span><br />
							<input style="width: 100%" type="text" id="txtName" />
						</td>
					</tr>
					<tr>
						<td>
							<!-- span fcklang="DlgFormAction">Action</span><br /> -->
							
							<input style="width: 100%" type="hidden" id="txtAction" />
						</td>
					</tr>
					<tr>
						<td>
							<span fcklang="dformchangepage">Redirect  Page</span><br />
							<input style="width: 100%" type="text" id="changepage" />
							<!--  span fcklang="DlgFormMethod">Method</span><br />
							<select id="txtMethod">
								<option value="get" selected="selected">GET</option>
								<option value="post">POST</option>
							</select>-->
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
 </div>
  <div id="SqlTab" align="center" width="100%" style="DISPLAY: none">
		<input id="SqlText1" type="text" size="100"/>
		<input id="SqlText2" type="text" size="100"/>
		<input id="SqlText3" type="text" size="100"/>
  </div>
</body>
</html>
