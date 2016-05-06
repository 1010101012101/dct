<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="cn.vlabs.duckling.vwb.*"%>
<%@ page import="cn.vlabs.duckling.vwb.ui.command.*" %>
<%
	VWBContext context = VWBContext.createContext(request,VWBCommand.ATTACH,null);
	String googlemapkey="";
	if(context.getProperty("googlemapkey")!=null&&!"".equals(context.getProperty("googlemapkey")));
		googlemapkey="&key="+context.getProperty("googlemapkey");
%>
<html>
  <head>
    <title>fck_demap.html</title>
	
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="this is my page">
    <meta http-equiv="content-type" content="text/html;charset=UTF-8">
    <script src="common/fck_dialog_common.js?version=dct4.5" type="text/javascript"></script>
    <script src="http://maps.google.com/maps?file=api&amp;v=2.x;&oe=utf-8<%=googlemapkey %>" 
      type="text/javascript"></script>

    
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
			
			
			
			
			var strsaveInfo=FCKLang.saveInfo;
			window.onload = function(){
				initialize()
				LoadSelection();
				oEditor.FCKLanguageManager.TranslatePage(document) ;
				dialog.SetOkButton( true ) ;
				dialog.SetAutoSize( true ) ;
			}

			function Ok(){
			
				addPlugin();
				return true ;
			}
			
			
			function showdiv(name){
				document.getElementById("pointy").disabled=''
 	 			document.getElementById("pointx").disabled=''
				document.getElementById("mainset").style.display="none"
				document.getElementById("pointset").style.display="none"
				document.getElementById("addressset").style.display="none"
				document.getElementById(name).style.display=""
				
				document.getElementById("pointx").value	=""
			 	document.getElementById("pointy").value	=""
			 	document.getElementById("pointinfo").value="";
			}
			
			
			function addPlugin(){
				oPlugin		= FCK.EditorDocument.createElement( 'img' ) ;
				oPlugin.className="plugin";	
				try{
					var innerStr=getInnerStr();
					oPlugin.src =FCKConfig.DucklingBaseHref+"scripts/fckeditor/editor/images/pluginimages/DEMap.jpg" ;
					oPlugin.title=innerStr;
					oPlugin.contentEditable = false;
				}catch(e){
					alert("e="+e);
				}
				changeSize(oPlugin)
				FCK.InsertElement( oPlugin ) ;
			}
			function changeSize(obj){
				if(GetE('containheight').value!="")
					obj.height=GetE('containheight').value;
				if(GetE('containwidth').value!="")
					obj.width=GetE('containwidth').value;
				if(GetE('containwidth').value==""&&GetE('containwidth').value=="")
					obj.removeAttribute("style");
			}
			
			function getInnerStr(){
				var pointlist=getPointList();
				pointlist=(pointlist+"").replace(new RegExp("\"","gm"),"@#@")
				pointlist=(pointlist+"").replace(new RegExp("=","gm"),"@!@")
				var scale=document.getElementById("scale").value;
				var containy=document.getElementById("containy").value;
				var containx=document.getElementById("containx").value;
				restr="name='cn.vlabs.duckling.dct.services.plugin.impl.DEMapPlugin';"
				if(pointlist!="")
					restr+="pointlist='"+pointlist+"';"
				if(scale!="")
					restr+="scale='"+scale+"';"
				if(containx!="")
					restr+="containx='"+containx+"';"
				if(containy!="")
					restr+="containy='"+containy+"';"
					
				var maptype=document.getElementById("maptype").value;
				restr+="maptype='"+maptype+"';";
				return restr;
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
					
					
					
					var getinnerStr=oImage.title;//
					var arrStr=getinnerStr.split(";");
					for(i=0;i<arrStr.length;i++){//
						if(arrStr[i]!=null&&trim(arrStr[i])!=""){
								var keyvalue=arrStr[i].split("=");
								if(trim(keyvalue[0])=='pointlist'){
									var getNamevalue=trim(keyvalue[1]).replace(/(^\')|(\'$)/g, "");  
									getNamevalue=(getNamevalue+"").replace(new RegExp("@!@","gm"),"=")
									getNamevalue=getNamevalue.substr(11,getNamevalue.length-12)
									
									//alert(getNamevalue)
									arr=getNamevalue.split("]")
									
									for(j=0;j<arr.length;j++){
										index=arr[j].indexOf("[")
										//alert(arr[j].indexOf("[")+"|"+arr[j])
										if(index!=-1){
											tempstr=arr[j].substr(index+1);
											index2=tempstr.indexOf("(")+1
											tempstr=tempstr.substr(index2)
											realarr=tempstr.split(",")
											x=realarr[0]
											y=realarr[1].substr(0,realarr[1].length-1)
											info=realarr[2].replace(new RegExp("@#@","gm"),"")
											
											
											
											imgurl=realarr[3].replace(new RegExp("@#@","gm"),"")
											addinput(x,y,info,imgurl)
										}
									}
								}else if(trim(keyvalue[0])=='scale'){
									var getNamevalue=trim(keyvalue[1]).replace(/(^\')|(\'$)/g, "");   
									document.getElementById('scale').value=getNamevalue;	
								}else if(trim(keyvalue[0])=='containx'){
									var getNamevalue=trim(keyvalue[1]).replace(/(^\')|(\'$)/g, "");   
									document.getElementById('containx').value=getNamevalue;			
								}else if(trim(keyvalue[0])=='containy'){
									var getNamevalue=trim(keyvalue[1]).replace(/(^\')|(\'$)/g, "");   
									document.getElementById('containy').value=getNamevalue;			
								}else if(trim(keyvalue[0])=='maptype'){
									var getNamevalue=trim(keyvalue[1]).replace(/(^\')|(\'$)/g, "");   
									document.getElementById('maptype').value=getNamevalue;		
									changemaptype(getNamevalue)	
								}
								
								
								if(document.getElementById('containy').value&&document.getElementById('containx').value&&document.getElementById('scale').value){
									myscale=Number(document.getElementById('scale').value)
									map.setCenter(new GLatLng(document.getElementById('containx').value,document.getElementById('containy').value),myscale); 
								}
						}	
					}	
					redrawMap()
					
		}
			
</script>
			
<script  type="text/javascript">


  

  var map;
  var geocoder;
  function initialize() {
	  map= new GMap2(document.getElementById("map_canvas"));     
	  geocoder = new GClientGeocoder();
	  map.setCenter(new GLatLng(40, 116), 6);   //地图坐标 三个参数分别为 "纬度" "经度" "比例尺"     
	  map.enableScrollWheelZoom();    //启用鼠标滚轮     
	//  map.addControl(new GMapTypeControl());     //地图种类  
	  map.addControl(new GSmallMapControl());    //放大缩小  
	  GEvent.addListener(map, "click", function(marker,point) {
			  if(marker) {
				  try{
					 if(marker.getLatLng()){
					 	var point=marker.getLatLng();
					 	//map.removeOverlay(marker)
					 	y=point.x
					 	x=point.y
					 	delinput(""+x+y+"")
					 };
				  }catch(e){ }
				
			  } else if(point){
				drawpoint(point);
			  }
	  });
  }





	function drawpoint(point){
			var html='<div><table cellspacing="1" cellpadding="1" width="100px" border="0" align="center" style="border-collapse:collapse">'+
			'<tr ><td  style="color:blue;"><input type="text" width="100px" id="infotext'+point.y+point.x+'"></td></tr>'+
			'<tr><td style="color:blue;"><input type="button" value="'+strsaveInfo+'"  onclick="javascript:saveInfo(\''+point.y+'\',\''+point.x+'\')"></td></tr>'+
			'</table></div>';    
			 var marker = new GMarker(point);
			 map.addOverlay(marker);
			 marker.openInfoWindowHtml(html);
			 y=point.x
			 x=point.y
			  addinput(x,y,"","");
	 
	}

	function createMarker(point, info){
			//创建标记内容及标记的鼠标事件     
			 var marker = new GMarker(point);     
			 var html = '<div>'+ info +'</div>';     
			 GEvent.addListener(marker, "mouseover", function() {     
				 marker.openInfoWindowHtml(html);     
			 });     
			 GEvent.addListener(marker, "mouseout", function() {     
				 marker.closeInfoWindow();     
			 });     
			 GEvent.addListener(marker, "click", function() {     
				 map.setCenter(point, 12);      
			 });    
			 return marker;     
		 }     

	 function saveInfo(x,y){
	 		info=document.getElementById("infotext"+x+y+"").value	
		 	replaceinput(x,y,info)
		 	findpoint(x,y)
		 	map.closeInfoWindow()
	 }
	 
	 function findpoint(x,y){
		var point=new GLatLng(x,y);
 	 	map.setCenter(point,6); 
 	 	showdiv('pointset');
 	 	document.getElementById("pointx").value	=x
 	 	document.getElementById("pointy").value	=y
 	 	document.getElementById("pointy").disabled='disabled'
 	 	document.getElementById("pointx").disabled='disabled'
 	 	document.getElementById("pointinfo").value=document.getElementById("atxtName"+x+y).title;
 	 	document.getElementById("pointimg").value=document.getElementById("atxtName"+x+y).getAttribute("imgurl");
	 }

function replaceinput(x,y,info,imgurl){

			info=info.replace(new RegExp(",","gm")," ")
			info=info.replace(new RegExp(";","gm")," ")
			info=info.replace(new RegExp("\"","gm")," ")
			info=info.replace(new RegExp("=","gm")," ")
			newTr=document.getElementById(""+x+y+"")
		 	
			while (newTr.firstChild) {
			      var oldNode = newTr.removeChild(newTr.firstChild);
			       oldNode = null;
			}


			var newNameTd=document.createElement('td');
			
			var newNameTxt=document.createElement('a');
			newNameTxt.innerHTML="x:"+(x+"").substr(0,6)+";y:"+(y+"").substr(0,6)
			newNameTxt.style.cursor="pointer";
			newNameTxt.onclick=function () {findpoint(x+"",y+"")}
			newNameTxt.id="atxtName"+x+y;
			newNameTxt.type="text";
			newNameTxt.title=info;
	
			newNameTxt.setAttribute("imgurl",imgurl);
				
				
			try{
				var newhiddenTxt=document.createElement('<input type="hidden" name="hiddenpoint"/>');
				newhiddenTxt.value="[new GLatLng("+x+","+y+"),\""+info+"\",\""+imgurl+"\"]"
			}catch(e){
				var newhiddenTxt=document.createElement('input');
				newhiddenTxt.type="hidden"
				newhiddenTxt.name="hiddenpoint";
				newhiddenTxt.value="[new GLatLng("+x+","+y+"),\""+info+"\",\""+imgurl+"\"]"
			
			}
			
			
			
			
			var newButton=document.createElement('input');
			newButton.id="button_";
			newButton.type="button";
			newButton.style.background="url(fck_dmlplugin/images/delete.gif) no-repeat";
			newButton.style.width="22px";
			newButton.style.height="22px";
			newButton.onclick=function () { delinput(newTr.id) }
			
			
			newTr.appendChild(newNameTd);
			
			newNameTd.appendChild(newhiddenTxt);
			newNameTd.appendChild(newButton);
			newNameTd.appendChild(newNameTxt);

}

function addinput(x,y,info,imgurl){
	info=info.replace(new RegExp("\"","gm")," ")
	info=info.replace(new RegExp("=","gm")," ")
	info=info.replace(new RegExp(",","gm")," ")
	info=info.replace(new RegExp(";","gm")," ")
	var table = document.getElementById("addinputtable");
	var numTr = table.getElementsByTagName("tr").length;
	var newTr=document.createElement('tr');
	newTr.id=""+x+y+"";//数字取得的规律是直接取tr的长度，因为在之前有一行tr做了文字说明，而后来的输入tr是用1开头的，所以不用再+1了
	var newNameTd=document.createElement('td');
	
	var newNameTxt=document.createElement('a');
	newNameTxt.innerHTML="x:"+(x+"").substr(0,6)+";y:"+(y+"").substr(0,6)
	newNameTxt.style.cursor="pointer";
	newNameTxt.onclick=function () {findpoint(x+"",y+"")}
	newNameTxt.id="atxtName"+x+y;
	newNameTxt.type="text";
	newNameTxt.title=info;
	newNameTxt.setAttribute("imgurl",imgurl);
	try{
		var newhiddenTxt=document.createElement('<input type="hidden" name="hiddenpoint"/>');
		newhiddenTxt.value="[new GLatLng("+x+","+y+"),\""+info+"\",\""+imgurl+"\"]"
	}catch(e){
		var newhiddenTxt=document.createElement('input');
		newhiddenTxt.type="hidden"
		newhiddenTxt.name="hiddenpoint";
		newhiddenTxt.value="[new GLatLng("+x+","+y+"),\""+info+"\",\""+imgurl+"\"]"
	
	}
	
	
	
	
	var newButton=document.createElement('input');
	newButton.id="button_"+(numTr);
	newButton.type="button";
	newButton.style.background="url(fck_dmlplugin/images/delete.gif) no-repeat";
	newButton.style.width="22px";
	newButton.style.height="22px";
	newButton.onclick=function () { delinput(newTr.id) }
	
	
	newTr.appendChild(newNameTd);
	
	newNameTd.appendChild(newhiddenTxt);
	newNameTd.appendChild(newButton);
	newNameTd.appendChild(newNameTxt);
	
	
	table.appendChild(newTr);
	
}


function delinput(objid){
	var table = document.getElementById("addinputtable");
	table.removeChild(document.getElementById(objid))
	redrawMap()
}


function redrawMap(){
	map.clearOverlays();
	strPointList=getPointList();
	PointList=eval(strPointList);
	for(i=0;i<PointList.length;i++){
		var point = PointList[i][0];
		var info=PointList[i][1];
		var imgurl=PointList[i][2];
		
		
		if(imgurl){
			var icon = new GIcon();
			icon.image =imgurl
			icon.iconAnchor = new GPoint(point.y,point.x);
			var marker = new GMarker(point, icon);
		}else{
			var marker = new GMarker(point);
		}
	
		
		//var marker = new GMarker(point,imgurl);
		map.addOverlay(marker);
		//map.addOverlay(createMarker(point,info));
	}
}






function getPointList(){
 var restr="PointList=["; 
 var hiddenpoints=document.getElementsByName("hiddenpoint");
 
 for(i=0;i<hiddenpoints.length;i++){
 if(i!=hiddenpoints.length-1)
 	restr+=hiddenpoints[i].value+","
 else
 	restr+=hiddenpoints[i].value
 }
 restr=restr+"]"
 return restr;
}

function showAddress() {
	 var showAddressvalue=document.getElementById("Address").value;
	 if (geocoder) {
		 geocoder.getLatLng(showAddressvalue,
		 function(point) {
			 if (!point) {
				 alert("We can't find that : " + showAddressvalue);
			 } else {
				 map.setCenter(point, 13);
				 var marker = new GMarker(point);
				 map.addOverlay(marker);
				 marker.openInfoWindowHtml(showAddressvalue);
				 y=point.x
				 x=point.y
				 addinput(x,y,showAddressvalue)
			 }
		});
	 }
	 document.getElementById("Address").value="";
} 


function addPointByXY(){
 y=document.getElementById("pointy").value;
 x=document.getElementById("pointx").value;
 imgurl=document.getElementById("pointimg").value;
//pointimg=pointimg.replace(new RegExp("http://","gm"),"")
 if(x==""||y=="")return;
 info=document.getElementById("pointinfo").value;
 var point=	new GLatLng(x,y)
 
 var icon = new GIcon();
icon.image =imgurl
icon.iconAnchor = new GPoint(x,y);
var marker = new GMarker(point, icon);
 
 


 if(document.getElementById(""+x+y)){
 	replaceinput(x,y,info,imgurl)
 }else{
  	map.addOverlay(marker);
 	addinput(x,y,info,imgurl)
 }

 map.setCenter(point, 6);
 document.getElementById("pointy").value="";
 document.getElementById("pointx").value="";
 document.getElementById("pointinfo").value="";
 document.getElementById("pointy").disabled=''
 document.getElementById("pointx").disabled=''
 document.getElementById("pointimg").value=""
 
 redrawMap()
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
			 
			 
			 
function changemaptype(value){

 map.setMapType(eval(value)); 

}			 
</script>
			
			
			
  </head>
  
  <body class="InnerBody" style="overflow: hidden">
    <div id="MainTab" align="left" width="100%" height="100%" >
     <table width="100%">
	  <tr>
	    <td valign="middle" align="left" width="70%">
	      <div id="map_canvas" class="map" style="width:600px;height:400px"></div>
	    </td>
	    <td  width="30%" valign="middle" align="center">
	    <table border="1" height="400px" width="100%">
		    <tr>
			    <td height="100px">
			    <div id="mainset">
				   	<table cellspacing="1" cellpadding="1" width="98%" border="1" height="100px" align="center" style="border-collapse:collapse;">
				   		 <tr>
							<td colspan="2">
							<span fcklang="DEMapSet">DEMapSet</span>
							</td>
						</tr>
						<tr>
							<td>
								<span fcklang="DlgTableHeight">Height</span>:
							</td>
							<td>
							<input id="containheight" style="width:50px" type="text"/>px
							</td>
							
						</tr>
						<tr>
							<td>
								<span fcklang="DlgTableWidth">Width</span>:
							</td>
							<td>
							<input id="containwidth" style="width:50px"  type="text"/>px
							</td>
						</tr>
						<tr>
							<td>
								<span fcklang="scale">scale</span>
							</td>
							<td>
							<select id="scale" style="width:50px" onchange="changemaptype(this.value)">
										<option value="1"  >1</option>
										<option value="2"  >2</option>
										<option value="3"  >3</option>
										<option value="4"  >4</option>
										<option value="5"  >5</option>
										<option value="6"  selected="selected">6</option>
										<option value="7"  >7</option>
										<option value="8"  >8</option>
										<option value="9"  >9</option>
										<option value="10"  >10</option>
										<option value="11"  >11</option>
										<option value="12"  >12</option>
										
										<option value="13"  >13</option>
										<option value="14"  >14</option>
										<option value="15"  >15</option>
										
										<option value="16"  >16</option>
										<option value="17"  >17</option>
										<option value="18"  >18</option>
										<option value="19"  >19</option>
										<option value="20"  >20</option>
										
										
								</select>
							
							<!--  input id="scale" style="width:50px"  type="text"/>(2-16)-->
							</td>
						</tr>
						<tr>
							<td>
								<span fcklang="centerx">centerx</span>
							</td>
							<td>
							<input id="containx" style="width:80px" type="text" value="40"/>
							</td>
							
						</tr>
						<tr>
							<td>
								<span fcklang="centery">centery</span>
							</td>
							<td>
							<input id="containy" style="width:80px" type="text" value="116"/>
							</td>
							
						</tr>
						
						<tr>
							<td>
								<span fcklang="maptype">Map Type</span>
							</td>
							<td>
								<select id="maptype" style="width:80px" onchange="changemaptype(this.value)">
										<option value="G_NORMAL_MAP" selected="selected" fcklang="normal_map">NORMAL MAP</option>
										<option value="G_HYBRID_MAP" fcklang="hybrid_map">HYBRID MAP</option> 
										<option value="G_SATELLITE_MAP" fcklang="satellite_map">SATELLITE MAP</option>
								</select>
							</td>
							
							
						</tr>
						
				   	</table>
				   	</div>
				   	
				   	
				   	<div id="pointset" style="display:none;">
				   	<table cellspacing="1" cellpadding="1" width="98%" border="1" height="100px" align="center" style="border-collapse:collapse;">
				   		 <tr>
							<td colspan="2">
							<span fcklang="pointset">pointset</span>
							</td>
						</tr>
						<tr>
							<td>
								x:
							</td>
							<td>
							<input id="pointx" style="width:100px" type="text"/>
							</td>
							
						</tr>
						<tr>
							<td>
								y:
							</td>
							<td>
							<input id="pointy" style="width:100px"  type="text"/>
							</td>
						</tr>
						<tr>
							<td>
								<span fcklang="DESCRIPTION">DESCRIPTION</span>
							</td>
							<td>
							<input id="pointinfo" style="width:150px"  type="text"/>
							</td>
						</tr>
						<tr>
							<td>
								<span fcklang="DlgImgTitle">IMG</span><span fcklang="DlgLnkURL">DlgLnkURL</span>
							</td>
							<td>
							<input id="pointimg" style="width:150px"  type="text"/>
							</td>
						</tr>
						<tr>
							<td>
								
							</td>
							<td>
							<input type="button"   fcklang="savepoint" onclick="addPointByXY()"/>
							</td>
						</tr>
						
				   	</table>
				   	</div>
				   	
				   	
				   	<div id="addressset" style="display:none;">
				   	<table cellspacing="1" cellpadding="1" width="98%" border="1" height="100px" align="center" style="border-collapse:collapse;">
				   		 <tr>
							<td colspan="2">
							<span fcklang="FindByAdd">Find By Address</span>
							</td>
						</tr>
						<tr>
							<td>
								<span fcklang="Address">Address</span>
							</td>
							<td>
							<input id="Address" style="width:150px" type="text"/>
							</td>
							
						</tr>
						<tr>
							<td>
								
							</td>
							<td>
							<input type="button"  fcklang="Find" onclick="showAddress()"/>
							</td>
						</tr>
						
				   	</table>
				   	</div>
				   	
				   	
			   		<table cellspacing="1" cellpadding="1" width="98%" border="1" height="100px" align="center" style="border-collapse:collapse;">
				   		<tr>
							<td colspan="2">
								<input type="button" fcklang="DEMapSet" onclick="showdiv('mainset')"/>
							</td>
						</tr>
				   		<tr>
							<td colspan="2">
								<input type="button" fcklang="pointset" onclick="showdiv('pointset')"/>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<input type="button"  fcklang="FindByAdd"  onclick="showdiv('addressset')"/>
							</td>
						</tr>
					</table>
			   	
			    </td>
		    </tr>
		    <tr>
			    <td valign="top">
			  



					<div style="overflow:scroll;height:150px;width:220px" >
						<table  cellspacing="0" cellpadding="0" border="1" width="100%">
							<tbody  id="addinputtable">
							<tr>
								<td align="center" class="DMLPF_header">
										<span fcklang="PointInfo">Point Info</span>
								</td>
							</tr>
							</tbody>
						</table>
					</div>



			    </td>
		    </tr>
	    </table>
	    </td>
	  </tr>
  	 </table>
  </div>
  </body>
</html>
