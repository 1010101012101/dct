<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean"
	prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic"
	prefix="logic"%>
<%@ page import="cn.vlabs.duckling.vwb.*"%>
<%@ page import="cn.vlabs.duckling.vwb.ui.command.*" %>
<%
	VWBContext context = VWBContext.createContext(request,VWBCommand.ATTACH,null);
	String actionPath = "clbUploadsAction.do";
	String basePath = context.getBaseURL()+"/";
	String userVo = context.getVO();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'fck_imagescroll.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script src="<%=basePath %>scripts/fckeditor/editor/dialog/common/fck_dialog_common.js" type="text/javascript"></script>
		<script type="text/javascript" src="<%=basePath %>scripts/ajax/ajax.js"></script>
	<script type="text/javascript">
			var dialog	= window.parent ;
			var oEditor = dialog.InnerDialogLoaded() ;
			var FCKLang	= oEditor.FCKLang ;
			var oDOM = oEditor.FCK.EditorDocument ;
			var FCK			= oEditor.FCK ;
			var FCKConfig	= oEditor.FCKConfig ;
			var oActiveEl = dialog.Selection.GetSelectedElement() ;
			
			var oImage = dialog.Selection.GetSelectedElement() ;
			if ( oImage && oImage.tagName != 'IMG' && !( oImage.tagName == 'INPUT' && oImage.type == 'image' ) )
				oImage = null ;
			
			
			window.onload = function(){
				LoadSelection();
				oEditor.FCKLanguageManager.TranslatePage(document) ;
				dialog.SetOkButton( true ) ;
				dialog.SetAutoSize( true ) ;
			}


			function Ok(){
				var path = document.getElementById("path").value;
				if(path==""){
					alert(FCKLang.imagescrollinfo)
					return false;
				}
				
				if(GetE('containwidth').value!=null&&isNaN(GetE('containwidth').value)){
					alert(FCKLang.DlgTableWidth+FCKLang.isNaNWarning)
					return false;
				}
				
				if(GetE('containheight').value!=null&&isNaN(GetE('containheight').value)){
					alert(FCKLang.DlgTableHeight+FCKLang.isNaNWarning)
					return false;
				}
				
				if(GetE('interval').value!=null&&isNaN(GetE('interval').value)){
					alert(FCKLang.interval+FCKLang.isNaNWarning)
					return false;
				}
				addPlugin()
				
				
				return true ;
			}
			
			function addPlugin(){
				oPlugin		= FCK.EditorDocument.createElement( 'img' ) ;
				oPlugin.className="plugin";	
				try{
					var innerStr=getInnerStr();
					
					oPlugin.src =FCKConfig.DucklingBaseHref+"scripts/fckeditor/editor/images/pluginimages/imagescr.jpg" ;
					oPlugin.title=innerStr;
					oPlugin.contentEditable = false;
				}catch(e){
					alert("e="+e);
				}
				changeSize(oPlugin)
				FCK.InsertElement( oPlugin ) ;
			}
			function getInnerStr(){
				var interval = document.getElementById("interval").value;
				var containheight = document.getElementById("containheight").value;
				var containwidth = document.getElementById("containwidth").value;
				var classname = document.getElementById("classname").value;
				var path = document.getElementById("path").value;
				restr="name='cn.vlabs.duckling.dct.services.plugin.impl.ImageScrollPlugin';"
				/*if(containheight!="")
					restr+="height='"+containheight+"';"
				if(containwidth!="")
					restr+="width='"+containwidth+"';"*/
				if(classname!="")
					restr+="classname='"+classname+"';"
				if(path!="")
					restr+="path='"+path+"';"
				if(interval!="")
					restr+="time='"+interval+"';"
				return restr;
			}
			function openpath(){
				GetE('PathTab').style.display = '' ;
				GetE('MainTab').style.display = 'none' ;
			}
			
			function getpath(str){
				GetE('path').value=str+"/*.*";
				GetE('PathTab').style.display = 'none' ;
				GetE('MainTab').style.display = '' ;
			}
			
			function changeSize(obj){
				if(GetE('containheight').value!="")
					obj.height=GetE('containheight').value;
				if(GetE('containwidth').value!="")
					obj.width=GetE('containwidth').value;
				if(GetE('containwidth').value==""&&GetE('containwidth').value=="")
					obj.removeAttribute("style");
			}
			

			function LoadSelection(){
				if ( ! oImage ) return;
				var iWidth, iHeight ;
				var regexSize = /^\s*(\d+)px\s*$/i ;
				if ( oImage.style.width )
					{
						var aMatchW  = oImage.style.width.match( regexSize ) ;
						if ( aMatchW )
						{
							iWidth = aMatchW[1] ;
							oImage.style.width = '' ;
							SetAttribute( oImage, 'width' , iWidth ) ;
						}
					}
				
				if ( oImage.style.height )
				{
					var aMatchH  = oImage.style.height.match( regexSize ) ;
					if ( aMatchH )
					{
						iHeight = aMatchH[1] ;
						oImage.style.height = '' ;
						SetAttribute( oImage, 'height', iHeight ) ;
					}
				}
				//alert(oImage.getAttribute("width")+"|"+GetAttribute( oImage, "width", '' ))
					GetE('containwidth').value	= oImage.getAttribute("width") ? oImage.getAttribute("width") : GetAttribute( oImage, "width", '' ) ;
					GetE('containheight').value	= oImage.getAttribute("height") ? oImage.getAttribute("height") : GetAttribute( oImage, "height", '' ) ;
					//if(!GetE('containwidth').value)GetE('containwidth').value=oImage.width;
					//if(!GetE('containheight').value)GetE('containheight').value=oImage.height;
					
					
					
					/*填充属性*/
					var getinnerStr=oImage.title;//取到plugin内容
					var arrStr=getinnerStr.split(";");
					for(i=0;i<arrStr.length;i++){//填充数据
						if(arrStr[i]!=null&&trim(arrStr[i])!=""){
								var keyvalue=arrStr[i].split("=");
								if(trim(keyvalue[0])=='time'){
									var getNamevalue=trim(keyvalue[1]).replace(/(^\')|(\'$)/g, "");   
									document.getElementById('interval').value=getNamevalue;
								}else if(trim(keyvalue[0])=='path'){
									var getNamevalue=trim(keyvalue[1]).replace(/(^\')|(\'$)/g, "");   
									document.getElementById('path').value=getNamevalue;	
								}else if(trim(keyvalue[0])=='classname'){
									var getNamevalue=trim(keyvalue[1]).replace(/(^\')|(\'$)/g, "");   
									document.getElementById('classname').value=getNamevalue;			
								}
						}	
					}	
			}
			
			
			
			
			function trim(str){  //删除左右两端的空格   
			  return str.replace(/(^\s*)|(\s*$)/g, "");   
			 }   
			 function ltrim(str){  //删除左边的空格   
			  return str.replace(/(^\s*)/g,"");   
			 }   
			 function rtrim(str){  //删除右边的空格   
			  return str.replace(/(\s*$)/g,"");   
			 }   
		</script>
  </head>
  
  <body class="InnerBody" style="overflow: hidden">
    <div id="MainTab" align="center" width="100%" height="100%" >
    <fieldset class="DE_dialogfieldsetstyle" style="text-align: center;"><legend  class="DE_legendfontstyle" fcklang="DMLPluginProValue" ></legend>
    <table cellspacing="1" cellpadding="1" width="98%" border="0" align="center" style="border-collapse:collapse;">
   		 <tr>
			<td width="20%">
				&nbsp;
			</td>
			<td width="35%">
			&nbsp;
			</td>
			<td width="20%">
				&nbsp;
			</td>
			<td width="25%">
			&nbsp;
			</td>
		</tr>
		<tr>
			<td>
				<span fcklang="DlgTableHeight">Height</span>:
			</td>
			<td>
			<input id="containheight" style="width:50px" type="text"/>px
			</td>
			<td>
				<span fcklang="DlgTableWidth">Width</span>:
			</td>
			<td>
			<input id="containwidth" style="width:50px"  type="text"/>px
			</td>
		</tr>
		<tr>
			<td>
				&nbsp;
			</td>
			<td>
			
			</td>
			<td>
				&nbsp;
			</td>
			<td>
			
			</td>
		</tr>
		<tr>
			<td >
				<span fcklang="interval">interval</span>:
			</td>
			<td >
			<input id="interval" style="width:50px" type="text"/><span  fcklang="MSEL">MSEL</span>
			</td>
			<td>
				<span fcklang="DRClass">Class name</span>:
			</td>
			<td>
			<input id="classname" style="width:50px" type="text"/>
			</td>
		</tr>
		<tr>
			<td>
				&nbsp;
			</td>
			<td>
			
			</td>
			<td>
				&nbsp;
			</td>
			<td>
			
			</td>
		</tr>
		<tr>
			<td colspan="4" >
				<span fcklang="resourcespath">resources path</span>
			
			<input id="path" type="text" style="width:120px"/><input type="button" onclick="openpath()"  fckLang="browse" />
			</td>
		</tr>
		</table>
		</fieldset>
		</div>
	
		<div id="PathTab" width="100%"  style="DISPLAY: none;overflow:scroll;height:200px;background: #FFFFFF;border:1px #999999 solid;">
		<span style="color:blue;" fcklang="imagescrollinfo">Plz choose</span>
		<table cellspacing="1" cellpadding="1" width="98%" border="0" align="center" style="border-collapse:collapse">
			<tr>
				<td width="10%" style="color:blue;"><img src="<%=basePath%>images/folder.png"></td>
				<td width="90%">&nbsp;</td>
			</tr>
			<logic:iterate id="result" name="results" scope="request" >
			<tr>
				<td></td>
				<td onclick="getpath('${result}')" style="cursor:pointer;color:blue;"><img src="<%=basePath%>images/votree/L.png"><img src="<%=basePath%>images/folder.png"><U>${result}</U></td>
			</tr>
		</logic:iterate>
		</table>
		</div>
	
  </body>
</html>
