<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="cn.vlabs.duckling.vwb.VWBContext"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="vourl">
	<vwb:Link format="url">
		<vwb:Param name="a">share</vwb:Param>
		<vwb:Param name="method">showVO</vwb:Param>
	</vwb:Link>
</c:set>
<%
	VWBContext context = VWBContext.getContext(request);
	String currentuser = context.getCurrentUser().getName();
    String umtvo = context.getVO();
%>

<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
<!--
function findEmailMessage() {
	var tab = document.getElementById('menu-VOUser');
	if (tab.className=='activetab'){
		return "sharepage.vo".localize();
	}else{
		return "sharepage.email".localize();
	}
}
//-->
</script>
<script type="text/javaScript">
function submitForm() {
	var email = document.SharePageForm.emailaddress.value.trim();
	var voemail = document.SharePageForm.objectIds.value.trim();
    
	if((email=="") && (voemail=="")){
	   alert(findEmailMessage());
	   document.SharePageForm.emailaddress.focus();
 	   return false;
	}
	if((email.indexOf(";")!=-1) || ((voemail.indexOf(";")!=-1))){
	   alert(findEmailMessage());
	   document.SharePageForm.emailaddress.focus();
 	   return false;
	}

	var emailaddress = email.replace(/\n/g, ",");
	emailaddress = emailaddress.replace(/\r/g, "");
	
	voemail = voemail.replace(/\n/g, ",");
	voemail = voemail.replace(/\r/g, "");
	
	if (!(emailaddress=="") && !verify(emailaddress)) {
		alert("sharepage.error.email".localize());
	   	document.SharePageForm.emailaddress.focus();
	   	return false;
	}

	document.SharePageForm.emailaddress.value = emailaddress;
	document.SharePageForm.objectIds.value = voemail;
	document.SharePageForm.checkedValue.disabled = false;
	//新加邮件title
	//document.sharePageForm.emailtitle = document.sharePageForm.emailtitle.value.trim();
	//document.SharePageForm.method = "senMail";
	document.SharePageForm.submit();
	
}
  //将字符串转化为数组
function split(original, regex)
{
	var startIndex = 0;
	var tempArray = new Array();
	var index = 0;
	startIndex = original.indexOf(regex);
	while(startIndex < original.length && startIndex != -1)
	{
	  temp = original.substring(index,startIndex);
	  tempArray.push(temp)
	  index = startIndex + regex.length;
	  startIndex = original.indexOf(regex,startIndex + regex.length);
	}
	tempArray.push(original.substring(index + 1 - regex.length));
	return tempArray;
}


//利用正则表达式进行验证
function verify(emailaddress)
{         
   	var myarray;  
   	//var patterns = /^([a-zA-Z0-9_-])+(\.([a-zA-Z0-9_-])+)+@([a-zA-Z0-9_-])+(\.([a-zA-Z0-9_-])+)+$/; 
   	var patterns = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
   
   	myarray = split(emailaddress, ",");
  
	for(i=0;i<myarray.length;i++)
    {    
		if(!patterns.test(myarray[i]))
            return false;
    }
    return true;
} 

function checkValue()  {
       
     	if (document.SharePageForm.objectIds.value.length != 0)
     		window.open('${vourl}&value='+encodeURI(document.SharePageForm.objectIds.value),'add','scrollbars=yes,resizable=yes,top=200,left=600,width=400,height=460');
     	else
     		window.open('${vourl}','add','scrollbars=yes,resizable=yes,top=200,left=600,width=400,height=460');
	 }

$(document).ready(function() {
var obj=$(".DCT_tabmenu>a")
	obj.each(function(i){
		this.onclick=function(){
			var strid=this.id
			var parentnode=this.parentNode.parentNode
			$(parentnode).children(".tabs").children("div").each(function(i){this.className="DCT_hidetab"})
			$(parentnode).children(".tabs").children("#"+strid.substr(5)).each(function(i){this.className=""})
			$(this.parentNode).children("a").each(function(i){this.className=""})
			this.className="activetab";
		}
	})
})

</script>
<h3><fmt:message key='sharepage.title' /></h3>

<form action="<vwb:LinkTo format="url"/>?a=share" method="post"
	enctype="multipart/form-data" name="SharePageForm">
	
	<input type="hidden" name="method" value="senMail"/>
	<p>&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key='sharepage.hint'>
		<fmt:param><%=umtvo%></fmt:param>
	</fmt:message></p>
	<input type="hidden" name="vo" id="vo" value="<%=umtvo%>" />
	<div class="sharpageborder"><vwb:TabbedSection
		defaultTab="VOUser">
		<fmt:message key="sharepage.choose.user" var="ChooseUser"/>
		<vwb:Tab id="VOUser"
			title='${ChooseUser}'>
			<div id="VOUser">
			<table width="98%" border="0" cellspacing="0" cellpadding="0"
				class="table-top">
				<tr>
					<td width="350px"><input type="hidden" id="objectIds"
						name="objectIds" value="" /> <textarea id="checkedValue"
						name="checkedValue" rows="5" cols="50" disabled></textarea></td>
					<td valign="top"><input type="button" name="selectVO"
						value="<fmt:message key='sharepage.choose.user'/>"
						onclick="checkValue()" />
					<div id="votree-grid"></div>
					<div id="votree-tree"></div>
					<div id="win-votree"></div>
					</td>
				</tr>
			</table>
			</div>
		</vwb:Tab>
		<fmt:message key="sharepage.email" var="shareEmail"/>
		<vwb:Tab id="otherUser" title="${shareEmail}">

			<table width="98%" border="0" cellspacing="0" cellpadding="0"
				class="table-top">
				<tr>
					<td><textarea name="emailaddress" rows="5" cols="50"></textarea>
					</td>
				</tr>
			</table>
		</vwb:Tab>
	</vwb:TabbedSection>

	<div id="tab2">
	<table width="98%" border="0" cellspacing="0" cellpadding="0"
		class="table-top">
		<tr>
			<th align="left" height="30"><font color="#C90384"><fmt:message
				key='sharepage.email.title' /></font></th>
		<tr>
			<td><textarea rows="2" cols="50" onfocus="doclear()" class="input textarea" name="emailTitle"><fmt:message key='sharepage.email.dearuser'><fmt:param value="<%=currentuser%>" /><fmt:param><vwb:viewportTitle /></fmt:param> </fmt:message></textarea></td>
		</tr>
		<tr>
			<td width="300" height="30" scope="row" align="left" valign="bottom">
				<span class="f14"><font color="#C90384"><fmt:message
					key='sharepage.desc' /></font></span>
			</td>
		</tr>
		<tr>
			<td><textarea name="emailContent" rows="5" cols="50"></textarea>
			</td>
		</tr>		
		<tr>
			<td width="700" class="td2" height="55"><span class="f15">
			&nbsp;&nbsp; <input type="hidden" name="page"
				value= '<vwb:viewportTitle />'/> <input type="button"
				name="okok" value="<fmt:message key='searchclb.confirm'/>"
				onclick="submitForm()"> <input type="button"
				value="<fmt:message key='version.return'/>" property="return"
				onclick="javascript:history.back(-1)" /></span></td>
		</tr>
	</table>
	</div>
	</div>
</form>