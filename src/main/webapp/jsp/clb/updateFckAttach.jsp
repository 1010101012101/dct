<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="cn.vlabs.duckling.vwb.tags.*"%>
<%@ page
	import="cn.vlabs.duckling.vwb.*"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean"	prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<% 

    
    VWBContext context = VWBContext.getContext(request);
    
 if (pageContext.getAttribute( VWBBaseTag.ATTR_CONTEXT, PageContext.REQUEST_SCOPE ) == null)
        	pageContext.setAttribute(VWBBaseTag.ATTR_CONTEXT, context, PageContext.REQUEST_SCOPE);
  String error=(String)request.getAttribute("error");
  if(error!=null){
     if(error.equals("PRIVILEGE_ERROR")){
       error="javascript.PRIVILEGE_ERROR";
     }else{
       error="error."+error;
     }
     
  }

%>
<link rel="stylesheet" type="text/css" href="scripts/fckeditor/editor/skins/office2003/fck_dialog.css"/>
<fmt:setBundle basename="templates.default"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">

<script type="text/JavaScript" language="JavaScript">
  
function fileSelect() {
	var filename = document.updateCLBForm.file.value;
	filename = filename.substring(filename.lastIndexOf('\\')+1);
	document.updateCLBForm.title.value=filename;
}
	    
 
//
 function checkUpdateValue() {
	    if (document.updateCLBForm.file.value==""){
	    	alert("<fmt:message key='updateFckAttach.selectFile'/>");   
		  	document.updateCLBForm.file.focus();
 		  	return false;
 		   
	    }
	    document.updateCLBForm.submit();
 }
 function callBack() {
	    window.parent.OnUploadCompleted( "0", "<bean:write name="updateCLBForm" property="attach" />", "<bean:write name="fileName" />", "<bean:write name="updateCLBForm" property="attach" />", "" );				     
 }
<logic:present name="succ">
	callBack();
</logic:present>
</script>
<div id="DucklingLnkUrl" >

<form name="updateCLBForm" action="<vwb:BaseURL/>/updateCLB.do" method="post"
	enctype="multipart/form-data">
 
			<input type="hidden" name="page" value="${page}"/> 
			<input type="hidden" name="attach" value="<bean:write name="updateCLBForm" property="attach" />" />
			<input type="hidden" name="docid" value="<bean:write name="updateCLBForm"  property="docid" />" />  
			<input type="hidden" name="comeFrom" value="Fck"/>
            <input type="hidden" name="act" value="upload"/>
            <%if(error!=null){ %>
               <div>
                 <font color="red"><fmt:message key='<%=error %>'/></font>
                  <input type="button" class="DuclingButton" value="<fmt:message key='updateFckAttach.cancel'/>" name="cancel" onclick="window.parent.window.parent.CloseDialog();"/>
               </div>
             <%}else{ %>
				<table width="100%">
				

		       <tr>
		          
					<td nowrap  align="right">
					
						<span><fmt:message key='updateFckAttach.currentVersion'/>:</span>
						 <br/>&nbsp;
					</td>
					
					<td   align="left">
				            <bean:write name="attachTitle" />
				              <br/>&nbsp;
					</td>
				</tr>
					<tr>
					 
						<td nowrap align="right" valign="top">
							<span><fmt:message key='updateFckAttach.selfile'/>:</span>
							   
						</td>
						
						<td align="left" >
							<input type="file" name="file" onkeydown="if (event.keyCode=='13') return false;" onchange="fileSelect()"
								 size="30%" />
								<br/>
								<fmt:message key='upload.maxsize'/>
								 
						</td>
					</tr>

					<tr>
						<td><br><br><br></td>
						<td align="right">
							<input id="btnOk" type="button" class="DuclingButton" value="<fmt:message key='updateFckAttach.ok'/>" name="update" onclick="checkUpdateValue()"/>
							<input id="btnCancel" type="button" class="DuclingButton" value="<fmt:message key='updateFckAttach.cancel'/>" name="cancel" onclick="window.parent.window.parent.CloseDialog();"/>
						</td>
					</tr>

				</table>
		
              <%}%>
</form>
 </div>