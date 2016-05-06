<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean"
	prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic"
	prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>

<fmt:setBundle basename="templates.default"/>
<logic:notEmpty name="errorInfo">
   <font color="red" size="3">
     <bean:write name="errorInfo"/>
   </font>
</logic:notEmpty>
<logic:empty name="errorInfo">
<%
	pageContext.setAttribute("locale", request.getLocale());
%>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/scripts/extjs/resources/css/ext-all.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/extjs/adapter/ext/ext-base.js"></script>	
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/extjs/split/page-ext.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/ajax/allpages.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/ajax/grantpage.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/ajax/CheckColumn.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/ajax/TreeCheckNodeUI.js"></script>
<script language=javascript>
initSearhPage("<vwb:Link jsp='allPages.do?containId=true' context='plain' format='url'/>");
<%=request.getAttribute("portalSetting")!=null?request.getAttribute("portalSetting"):""%>
function secBoard(n)
{
  document.getElementById("page_setting_parentpage").className="page_setting_sec1";
  document.getElementById("page_setting_pagepriv").className="page_setting_sec1";
  document.getElementById("page_setting_navpage").className="page_setting_sec1";
  document.getElementById("page_setting_leftpage").className="page_setting_sec1";
  document.getElementById("page_setting_banner").className="page_setting_sec1";
  document.getElementById("page_setting_footer").className="page_setting_sec1";
  document.getElementById("page_setting_trail").className="page_setting_sec1";
  
  
  document.getElementById(n).className="page_setting_sec2";
  n=n+"show";
  document.getElementById('page_setting_parentpageshow').style.display="none";
  document.getElementById('page_setting_pageprivshow').style.display="none";
  document.getElementById('page_setting_navpageshow').style.display="none";
  document.getElementById('page_setting_leftpageshow').style.display="none";
  document.getElementById('page_setting_bannershow').style.display="none";
  document.getElementById('page_setting_footershow').style.display="none";
  document.getElementById('page_setting_trailshow').style.display="none";
  document.getElementById(n).style.display="block";
}
</script>
<form action="${saveUrlForSetting}" id="pageSetting" method="post">
<fmt:message key="current.page.name" />：${currentSettingPage.title}
<br/>
<br/>
<table border="0" cellspacing="0" cellpadding="0" width="720px" id="page_setting_secTable">
    <tr height="20px" align=center> 
     <td id='page_setting_parentpage' class="page_setting_sec2" width="12%" onclick="secBoard('page_setting_parentpage')"><fmt:message key="page.setting.parentpage" /></td>
     <td id='page_setting_pagepriv' class="page_setting_sec1" width="15%" onclick="secBoard('page_setting_pagepriv')"><fmt:message key="page.setting.pagepriv" /></td>
     <td id='page_setting_navpage' class="page_setting_sec1" width="12%" onclick="secBoard('page_setting_navpage')"><fmt:message key="page.setting.navpage" /></td>
     <td id='page_setting_leftpage' class="page_setting_sec1" width="15%" onclick="secBoard('page_setting_leftpage')"><fmt:message key="page.setting.leftpage" /></td>
     <td id='page_setting_banner' class="page_setting_sec1" width="13%" onclick="secBoard('page_setting_banner')">Banner</td>
     <td id='page_setting_footer' class="page_setting_sec1" width="13%" onclick="secBoard('page_setting_footer')"><fmt:message key="page.setting.footer.show" /></td>
      <td id='page_setting_trail' class="page_setting_sec1" width="15%" onclick="secBoard('page_setting_trail')"><fmt:message key="page.setting.trail.show" /></td>
    
    </tr>
</table>
<table border="0" cellspacing="0" cellpadding="0" width="720px" height=240 id="mainTable" class="main_tab">
    <tbody id='page_setting_parentpageshow' style="display:none;"> 
    <tr> 
     <td valign=top>
        <br/>
        <table>
		  <tr>
		  <td align="left">
		  	<fmt:message key="setting.parent.page" />: 
		  </td>
		  <td><input ${root_page} type="text" name="parentPageTitle" id="parentPageTitle" class="inputText" value="${parentPageTitle}"/></td>
		  <td>
		 	  	<c:if test="${parentError}">
		  		<c:set var="page_setting_error_sec">page_setting_parentpage</c:set>
		  		<font color="red"><fmt:message key="${parentError}" /> </font>
		  		</c:if>
		  	</td>
		  </tr>
		</table>
     </td>
    </tr>
    </tbody> 
    <tbody id='page_setting_pageprivshow' style="display:none;"> 
    <tr> 
     <td valign=top> 
     <table>
		  <tr><td align="left"><fmt:message key="page.permission" /></td></tr>
		  <c:if test="${root_page==null}">
		  <tr>
		     <td >&nbsp;&nbsp;
		     <c:if test="${inheritPrv=='yes'}">
		     	<c:set var="radio_inheritPrv">checked="checked"</c:set>
		     </c:if>
		       <input ${root_page} type="radio" name="inheritPrv" ${radio_inheritPrv} value="yes" id="inheritPrvYes" onclick="showPrivs('no')"/> 
		       	<fmt:message key="inherit.parent.permission" /> 
		      </td> 
		  </tr>
		   </c:if>
		  <tr>
		      <td>&nbsp;&nbsp;
		      <% if("no".equals(request.getAttribute("inheritPrv"))){
		                     request.setAttribute("radio_oninheritPrv","checked=\"checked\"");
		              } %>
		       <input  type="radio" name="inheritPrv" ${radio_oninheritPrv} value="no" onclick="showPrivs('yes')" id="inheritPrvNo"/> <fmt:message key="use.itself.permission" /> 
		      </td> 
		  </tr>
		
		  <tr>
			  <td>
			           <div id="privsShow">
							<input type="hidden" id="page" name="page"value="${currentSettingPage.resourceId}" />
							<input type="hidden" id="pagePrivilege" name="pagePrivilege"value="" />
							<input type="hidden" name="vo" id="vo" value="${voGroup}" />
							<input type="hidden" id="selectedPrivs" name="selectedPrivs" value="" />
							<input type="hidden" id="allow" name="allow" value="${allow}" />
							<div id="pagePrivs-grid"></div>
							<div id="votree-grid"></div>
							<div id="votree-tree"></div>
							<div id="win-votree"></div>
						</div>
						<c:if test="${inheritPriv=='yes'}">
						     <script language="javascript" type="text/javascript">
		                        document.getElementById('privsShow').style.display ="none" ;
		                     </script>
						</c:if>
			  </td>
		  </tr>
		</table>
     </td>
    </tr>
    </tbody> <tbody id='page_setting_navpageshow' style="display:none;"> 
    <tr> 
     <td valign=top>
	     <table>
		   <tr><td colspan="3"><fmt:message key="setting.nav.page" /></td></tr>
		   <tr>
		       <td colspan="3">&nbsp;&nbsp;
		       <c:if test="${inheritNav=='yes'}">
		       		<c:set var="radio_inheritNav">checked="checked"</c:set>
		       </c:if>
		        <input ${root_page} type="radio" name="inheritNav" ${radio_inheritNav} value="yes"/><fmt:message key="inherit.parent.nav" />
		       </td>
		    </tr>
		   <tr>
		       <td>&nbsp;&nbsp;
		       <c:if test="${inheritNav=='new'}">
		       	<c:set var="radio_newNav">checked="checked"</c:set>
		       </c:if>
		       <input type="radio" name="inheritNav" value="new" ${radio_newNav}><fmt:message key="new.nav.page" />
		       </td>
		       <td>
		           <input name="newNavPageTitle" id="newNavPageTitle" class="inputText"/>
		       </td>
		       <td> 
               </td> 
           </tr>
		   <tr>
		       <td>&nbsp;&nbsp;
		       <c:if test="${inheritNav=='select'}">
		       		<c:set var="radio_selectNav">checked="checked"</c:set>
		       </c:if>
		        <input type="radio" name="inheritNav" value="select" ${radio_selectNav}> 
		        <fmt:message key="select.existent.nav" /> 
		       </td>
		       <td><input name="selectNavPageTitle" id="selectNavPageTitle" class="inputText" value="${selectNavPageTitle}"/>
		       </td>
		       <td> 
		       	<c:if test="${selectNavPageError!=null}">
		       		<c:set var="page_setting_error_sec">page_setting_navpage</c:set>
		       		<font color="red"><fmt:message key="${selectNavPageError}"/> </font>
		       	</c:if>
               </td> 
             </tr>
           <tr>
			     <td colspan="3">
					&nbsp;&nbsp;
					<c:if test="${inheritNav=='noTop'}">
					     <input type="radio" checked="checked" name="inheritNav"  value="noTop" />
					</c:if>
					<c:if test="${inheritNav!='noTop'}">
					     <input type="radio" name="inheritNav" value="noTop" />
					</c:if>
					<fmt:message key="page.settting.noTop" />
				</td>
		   </tr>
		   		<c:if test="${newNavPageError}">
		   			<c:set var="page_setting_error_sec">page_setting_navpage</c:set>
		   			<tr> <td colspan="3">&nbsp;&nbsp;&nbsp;&nbsp;<font color="red"><fmt:message key="${newNavPageError}"/> </font></td></tr> 
		   		</c:if>
		 </table>
      </td>
    </tr>
    </tbody> 
    <tbody id='page_setting_leftpageshow' style="display:none;"> 
    <tr> 
     <td valign=top>
	     <table>
		   <tr><td colspan="3"><fmt:message key="setting.left.page" /></td></tr>
		   <tr>
		        <td colspan="3">&nbsp;&nbsp;
		        	<c:if test="${inheritLeft=='yes'}">
		        		<c:set var="radio_inheritLeft">checked="checked"</c:set>
		        	</c:if>
		         	<input type="radio"  ${root_page} name="inheritLeft" value="yes" ${radio_inheritLeft}/>
		         	<fmt:message key="inherit.parent.leftMenu" />
		       </td> 
		    </tr>
		    <tr>
		        <td>&nbsp;&nbsp;
		        	<c:if test="${inheritLeft=='new'}">
		        		<c:set var="radio_newLeft">checked="checked"</c:set>
		        	</c:if>
		          <input type="radio" name="inheritLeft" value="new" ${radio_newLeft}/>
		           <fmt:message key="new.left.page" /> 
		        </td>
		        <td><input name="newLeftPageTitle" id="newLeftPageTitle" class="inputText"/>
		        </td>
		        <td>
                </td>
              </tr>
			    <%
			 	if (request.getAttribute("newLeftPageError") != null) {
			 	request.setAttribute("page_setting_error_sec","page_setting_leftpage");
			 %> <tr><td  colspan="3">&nbsp;&nbsp;&nbsp;&nbsp;
			  <font color="red"><fmt:message key="${newLeftPageError}" /> </font> </td></tr><%
			 	}
			 %>
		   <tr>
		          <td>&nbsp;&nbsp;
		              <% if("select".equals(request.getAttribute("inheritLeft"))){
		                     request.setAttribute("radio_selectLeft","checked=\"checked\"");
		              } %>
		              <input type="radio" name="inheritLeft" value="select" ${radio_selectLeft}/>
		              <fmt:message key="select.existent.leftMenu" />
		           </td>
		           <td>
		               <input name="selectLeftPageTitle" id="selectLeftPageTitle" class="inputText" value="${selectLeftPageTitle}"/>
		           </td>
		           <td><%
					   	if (request.getAttribute("selectLeftPageError") != null) {
					   	request.setAttribute("page_setting_error_sec","page_setting_leftpage");
					   %> <font color="red"><fmt:message key="${selectLeftPageError}" /> </font> <%
						 	}
						 %>
				   </td>
                  </tr>
		  
		   <tr>
		      <td colspan="3">&nbsp;&nbsp;
		        <% if("noLeft".equals(request.getAttribute("inheritLeft"))){
		                     request.setAttribute("radio_noLeft","checked=\"checked\"");
		              } %>
		        <input type="radio" name="inheritLeft" value="noLeft" ${radio_noLeft}/><fmt:message key="select.noleft.page" />
		      </td>
		   </tr>
		</table>
     </td>
    </tr>
    </tbody> 
    <tbody id='page_setting_bannershow' style="display:none;"> 
    <tr> 
     <td valign="top">
		<table>
		   <tr><td colspan="2"><fmt:message key="setting.banner" /></td></tr>
		   <tr>
		        <td colspan="2">&nbsp;&nbsp;
		          <% if("yes".equals(request.getAttribute("inheritBanner"))){
		                     request.setAttribute("radio_inheritBanner","checked=\"checked\"");
		              } %>
		          <input type="radio"  ${root_page} ${radio_inheritBanner} name="inheritBanner" value="yes"/><fmt:message key="inherit.parent.banner" />
		        </td> 
		   </tr>
		   <tr>
		        <td>&nbsp;&nbsp;
		        <% if("select".equals(request.getAttribute("inheritBanner"))){
		                     request.setAttribute("radio_selectBanner","checked=\"checked\"");
		              } 
		            if("true".equals(request.getAttribute("noBannersFlag"))){
		                     request.setAttribute("disabled_selectBanner","disabled=\"true\"");
		              } 
		              
		              %>
		         <input  type="radio" ${radio_selectBanner} name="inheritBanner"  value="select" ${disabled_selectBanner}/><fmt:message key="select.existent.banner" />
		        </td>
		        <td>
		        <select name="selectBanner" ${disabled_selectBanner} >
		          <c:forEach var="item" items='${requestScope.bannerList}' varStatus="status">
		             <c:if test="${item.selected}">
		             <option selected="selected" value="${item.id }">${item.name}</option>
		             </c:if>
		             <c:if test="${!item.selected}">
		             <option value="${item.id }">${item.name}</option>
		             </c:if>
		          </c:forEach>
		        </select>
		        <vwb:Permission permission="allPermission">
		        &nbsp;<input type='button' value="<fmt:message key='pagesetting.new.banner' /> " 
		        onclick="window.location='<vwb:Link page="5008" format="url"><vwb:Param name="method">newBanner</vwb:Param></vwb:Link>'"/>
		        </vwb:Permission>
		        </td> 
		   </tr>
		   <tr>
			     <td colspan="2">
					&nbsp;&nbsp;
					<c:if test="${inheritBanner=='noBanner'}">
					     <input type="radio" checked="checked" name="inheritBanner"  value="noBanner" />
					</c:if>
					<c:if test="${inheritBanner!='noBanner'}">
					     <input type="radio" name="inheritBanner" value="noBanner" />
					</c:if>
					<fmt:message key="page.settting.noBanner" />
				</td>
		   </tr>
		   
		   <tr>
			     <td colspan="2">
					&nbsp;&nbsp;
					<c:if test="${inheritBanner=='skinBanner'}">
					     <input type="radio" checked="checked" name="inheritBanner"  value="skinBanner" />
					</c:if>
					<c:if test="${inheritBanner!='skinBanner'}">
					     <input type="radio" name="inheritBanner" value="skinBanner" />
					</c:if>
					<fmt:message key="page.settting.skinBanner" />
				</td>
		   </tr>
		</table>
     </td>
    </tr>
    </tbody> 
    <tbody id='page_setting_footershow' style="display:none;"> 
        <tr>
			<td valign=top>
				<table>
					<tr>
						<td colspan="3">
							<fmt:message key="setting.footer.page" />
						</td>
					</tr>
					<tr>
						<td colspan="3">
							&nbsp;&nbsp;
							<c:if test="${inheritFooter=='yes'}">
							     <input type="radio" ${root_page} checked="checked" name="inheritFooter" value="yes" />
							</c:if>
							<c:if test="${inheritFooter!='yes'}">
							     <input type="radio" ${root_page} name="inheritFooter" value="yes" />
							</c:if>
							<fmt:message key="inherit.parent.footer" />
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;&nbsp;
							<c:if test="${inheritFooter=='select'}">
							     <input type="radio" checked="checked" name="inheritFooter"  value="select" />
							</c:if>
							<c:if test="${inheritFooter!='select'}">
							     <input type="radio" name="inheritFooter" value="select" />
							</c:if>
							<fmt:message key="select.existent.footer" />
						</td>
						<td>
							<input type="text" name="selectFooterTitle" id="selectFooterTitle" class="inputText" value="${selectFooterTitle}"/>
						</td>
						<td>
							<%
								if (request.getAttribute("selectFooterPageError") != null) {
								request.setAttribute("page_setting_error_sec","page_setting_footer");
							%>
							<font color="red">
							  <fmt:message key="${selectFooterPageError}" />
							</font>
							<%
								}
							%>
						</td>
					</tr>
					<tr>
						<td colspan="3">
							&nbsp;&nbsp;
							<c:if test="${inheritFooter=='noFooter'}">
							     <input type="radio" checked="checked" name="inheritFooter"  value="noFooter" />
							</c:if>
							<c:if test="${inheritFooter!='noFooter'}">
							     <input type="radio" name="inheritFooter" value="noFooter" />
							</c:if>
							<fmt:message key="page.settting.noFooter" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
    </tbody> 
    <tbody id='page_setting_trailshow' style="display:none;"> 
        <tr>
			<td valign=top>
				<table>
					<tr>
						<td>
							<fmt:message key="setting.trail.page" />
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;&nbsp;
							<c:if test="${inheritTrail=='yes'}">
							     <input type="radio" ${root_page} checked="checked" name="inheritTrail" value="yes" />
							</c:if>
							<c:if test="${inheritTrail!='yes'}">
							     <input type="radio" ${root_page} name="inheritTrail" value="yes" />
							</c:if>
							<fmt:message key="inherit.parent.trail" />
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;&nbsp;
							<c:if test="${inheritTrail=='settting'}">
							     <input type="radio" checked="checked" name="inheritTrail"  value="settting" />
							</c:if>
							<c:if test="${inheritTrail!='settting'}">
							     <input type="radio" name="inheritTrail" value="settting" />
							</c:if>
							<fmt:message key="page.setting.trail" />
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;&nbsp;
							<c:if test="${inheritTrail=='notrail'}">
							     <input type="radio" checked="checked" name="inheritTrail"  value="notrail" />
							</c:if>
							<c:if test="${inheritTrail!='notrail'}">
							     <input type="radio" name="inheritTrail" value="notrail" />
							</c:if>
							<fmt:message key="page.settting.notrail" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
    </tbody> 
    
   </table>

<table>
   <tr> <td width="250"> &nbsp; </td>  <td>  <html:submit><fmt:message key="page.setting.submit" /> </html:submit>  &nbsp;&nbsp;&nbsp;  
   <input type='button' value="<fmt:message key='version.return' /> "
   onclick="window.location='<vwb:Link page='${currentSettingPage.resourceId}' format='url'/>'"/> </td></tr>
</table>
</form>
<script language="javascript" type="text/javascript">

function showPrivs(flag){
     if(flag=='yes')
     {
       document.getElementById('privsShow').style.display ="";
     }
     else
     {
       document.getElementById('privsShow').style.display ="none" ;
     }
	 
}
var urlToModule = "<%=request.getContextPath()%>/";

 var transferToallPages ='<%=request.getAttribute("page_setting_error_sec")!=null?request.getAttribute("page_setting_error_sec"):"page_setting_parentpage" %>';

</script>
</logic:empty>
