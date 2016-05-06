<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean"
	prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic"
	prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script type="text/javascript" src="<%=request.getContextPath() %>/scripts/ajax/ScriptHelper.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/scripts/jquery/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/scripts/jquery/jquery-ui-1.7.2.custom.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/scripts/jquery/jquery.editinplace.js"></script>
<script type="text/javascript">
<!--
	$(document).ready(function(){
		//EDIT GROUPS
		$("td.editTitle").live('mouseover',function(){
			var titleName=$(this).attr("titleName");
			$(this).editInPlace({
				url:"5008?method=saveBannerTitle&bannerId=${updateBanner.id}",
				params:"titleName="+titleName,
				save_button:		'<button class="inplace_save">保存</button>',
				text_size:50,
				success:function(data){
				}
			});
		});
		
	});
//-->
</script>
<fmt:setBundle basename="templates.default"/>
<div id="Editor_b_di" class="Editor_b_di"
	style="position: absolute; top: 160px; display: none; left: 350px;z-index:11000;" >
	<form action="5008?method=updateBannerPicture" Id="update_banner_id" method="post" enctype="multipart/form-data">
	<input name="bannerId" type="hidden" value="${updateBanner.id}" />
	<div class="Editor_Bn_c" id="Editor_Bn_c" style="cursor:move" >
		<div class="Editor_bn_top">
			<div class="Editor_bn_logo"></div>
			<div class="Editor_bn_shift">
			    <fmt:message key="banner.update.upload" />
			</div>
			<div class="Editor_bn_Button">
				<a onclick="ScriptHelper.closeDialog('Editor_b_di')" href="#"><img src="<%=request.getContextPath() %>/images/close_img_03.gif" />
				</a>
			</div>
		</div>
	</div>
	<div class="Editor_center">
		<table>
		  <tr>
		     <td align="center"><div id="banner_upload_file_container"> </div></td>
		  </tr>
		</table>
	</div>
	<div class="Editor_d">
		<div align="right">
		  <html:submit><fmt:message key="new.page.ok" /></html:submit>&nbsp;&nbsp;&nbsp;&nbsp; <input type="button" onclick="ScriptHelper.closeDialog('Editor_b_di')" value="<fmt:message key="new.page.cancel" />" >&nbsp;&nbsp;&nbsp;&nbsp;
		</div>
	</div>
	</form>
</div>
<br>
<br>
      <div class="banner_manager_div_1"> <fmt:message key="banner.manager.bannerName" />: <c:out value="${bannerForm.map['bannerName']}"></c:out> </div>

 <table class="banner_manager_table_2" border="1" cellpadding="0" cellspacing="0">
      <c:if test="${updateBanner.bannerTitle>0}">
      <tr>
        <td width="70px"><fmt:message key="banner.update.title" />：</td><td><c:out value="${bannerTitleContent}" escapeXml="false"></c:out></td><td width="50px"> &nbsp;&nbsp;<a href="<c:out value='${updateBanner.bannerTitle}'/>"><img src="<%=request.getContextPath() %>/images/edit.png"/> </a>&nbsp;</td>
      </tr>
      </c:if>
      <c:if test="${updateBanner.leftPictureClbId>0}">
        <tr>
          <td><fmt:message key="banner.update.picture" />：</td><td> &nbsp;<img  height="80" src="${updateBanner.leftPictureUrl }"/></a>&nbsp; </td>
        </tr>
      </c:if>
      <c:if test="${updateBanner.middlePictureClbId>0}">
        <tr>
          <td><fmt:message key="banner.update.picture" />：</td><td> &nbsp;<img  height="80" src="${updateBanner.middlePictureUrl }"/></a>&nbsp; </td>
        </tr>
      </c:if>
      <c:if test="${updateBanner.rightPictureClbId>0}">
        <tr>
          <td><fmt:message key="banner.update.picture" />：</td><td> &nbsp;<img  height="80" src="${updateBanner.rightPictureUrl }"/></a>&nbsp; </td>
        </tr>
      </c:if>
   <%--    <c:if test="${updateBanner.tempMiddleName!=null && fn:length(updateBanner.tempMiddleName)>0}">
        <tr>
          <td><fmt:message key="banner.update.picture" />：</td><td > &nbsp;<img  height="80"  src="${tempRealBannerRoot}/images/<c:out value='${updateBanner.tempMiddleName}'/>">   </td><td>&nbsp; <a onclick="insertuploadfile('middleFile');ScriptHelper.showDialog('Editor_b_di', 'Editor_Bn_c');return false" title="<fmt:message key="banner.update.tip" />" href=#><img src="<%=request.getContextPath() %>/images/updatetip.gif"/></a>&nbsp; </td>
        </tr>
      </c:if>
      <c:if test="${updateBanner.tempRightName!=null&& fn:length(updateBanner.tempRightName)>0}">
        <tr>
          <td><fmt:message key="banner.update.picture" />：</td><td>&nbsp; <img  height="80"  src="${tempRealBannerRoot}/images/<c:out value='${updateBanner.tempRightName}'/>">   </td><td>&nbsp; <a onclick="insertuploadfile('rightFile');ScriptHelper.showDialog('Editor_b_di', 'Editor_Bn_c');return false" title="<fmt:message key="banner.update.tip" />" href=#><img src="<%=request.getContextPath() %>/images/updatetip.gif"/></a>&nbsp; </td>
        </tr>
      </c:if> --%>
      <c:if test="${updateBanner.firstTitle!=null}">
        <tr>
          <td>第一标题：</td><td colspan="2"  class="editTitle" titleName="firstTitle">${updateBanner.firstTitle }&nbsp;&nbsp;   </td>
        </tr>
      </c:if>
      <c:if test="${updateBanner.secondTitle!=null}">
        <tr>
          <td>第二标题：</td><td colspan="2" class="editTitle" titleName="secondTitle">${updateBanner.secondTitle }&nbsp;&nbsp; </td>
        </tr>
      </c:if>
      <c:if test="${updateBanner.thirdTitle!=null}">
        <tr>
          <td>第三标题：</td><td colspan="2"  class="editTitle" titleName="thirdTitle">${updateBanner.thirdTitle }&nbsp;&nbsp;   </td>
        </tr>
      </c:if>
  </table>
   <table>
    <tr> <td colspan="3" align="center"><br/>
         <input type='button' value="<fmt:message key='version.return' />" onclick="window.location.href='5008?method=showBanners';"/>
         </td> 
    </tr>
 </table>
  <script language="javascript" type="text/javascript">
    function insertuploadfile(filename)
    {
      var divcontainer = document.getElementById("banner_upload_file_container");
      divcontainer.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='file' name='"+filename+"'/>";
    }
</script>
