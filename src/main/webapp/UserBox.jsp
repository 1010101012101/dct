<%@ page language="java" import="cn.vlabs.duckling.vwb.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
//add  by  diyanliang 09-4-20 for FullScreen on Cookie
var cancelfullscrbutton='<fmt:message>public.fullscreen.cancel</fmt:message>';
var fullscrbutton='<fmt:message>public.fullscreen</fmt:message>';

window.onload=init;
function init(){
	initHTMLforFullScr(cancelfullscrbutton);
}
//end 09-4-20
var groupListTimeOutVar = null
function apploginfunc(str){
	if (groupListTimeOutVar != null)window.clearInterval(groupListTimeOutVar);
	if($(str).is(":hidden")){
		$(str).show("fast");
	}else{
		$(str).hide();
	}

}
function hidemygroupList(){
	if (groupListTimeOutVar != null)
		window.clearInterval(groupListTimeOutVar);
	groupListTimeOutVar = window.setInterval("document.getElementById('mygroupList').style.display = 'none'", 800);
}
function showmygroupList(){
	if (groupListTimeOutVar != null) {
		window.clearInterval(groupListTimeOutVar);
		groupListTimeOutVar = null;
	};
	$('#mygroupList').show("fast");
}
function changeStatus(){
}
</script>
<style>
.ppcontent {
    height: 128px;
    /*padding: 7px 0 0 8px;*/
    width: 238px;
}
.passportc {
    background: none repeat scroll 0 0 #FFFFFF;
    border: 1px solid #FFA200;
    font-size: 12px;
    height: 108px;
    text-align: left;
    width: 238px;
}

.passportc .card {
    color: #313031;
    font-weight: normal;
    padding: 0 0 0 25px;
}
.passportc ul, .passportc ol, .passportc li {
  color: #313031;
    list-style: none outside none;
}

</style>
<div class="DCT_Login_di" id="DCT_Login_di">
<%-- 
	<!-- //科技网项目要求加入的功能  start  -->
	<vwb:UserCheck status="authenticated">
	<div id="CSTNET" onmouseout="hidemygroupList()" >
	
	<span id="mygroupbutton" onmousedown="apploginfunc('#mygroupList')"><fmt:message key="duckling.userbox.myvo" /></span>
	<div style="display:none;" id="mygroupList" class="mygroupList DCT_hideoutmenu" onmouseover="showmygroupList()">   
	<vwb:SubPage pageid="1(7,6,5,4,3);2"/> 
	</div>
	<c:if test="${PORTAL_SESSION!=null&&PORTAL_SESSION.currentUser.authBy!=null&&'umt'!=PORTAL_SESSION.currentUser.authBy}">
	    <% String umt = VWBContainerImpl.findContainer().getConfig().getProperty("duckling.umt.site");
	        request.setAttribute("logincstnetURL",umt+"/user/loginThirdPartyApp");
	        request.setAttribute("loginOnlineStorageURL",umt+"/user/onlineStorageLoginServlet");
	     %>
		<ul class="DCT_Landing_ul">
			<li><a href="${logincstnetURL}" target="_blank">科技网邮箱 </a></li>
			<li><a href="${loginOnlineStorageURL}">网络硬盘 </a></li>
		</ul>
	</c:if>
	</div>
	</vwb:UserCheck>
	 <!--  //科技网项目要求加入的功能 end  -->

--%>	
	<div class="DCT_Landing">
		<ul class="DCT_Landing_ul">
			<vwb:UserCheck status="anonymous">
				<li>
					<fmt:message key="fav.greet.anonymous" />
				</li>
			</vwb:UserCheck>
			<vwb:UserCheck status="asserted">
				<li>
					<fmt:message key="fav.greet.asserted">
						<fmt:param>
							<vwb:UserTrueName />
						</fmt:param>
					</fmt:message>
				</li>
			</vwb:UserCheck>
			<vwb:UserCheck status="authenticated">
				<li>
					<fmt:message key="fav.greet.authenticated">
						<fmt:param>
							<vwb:UserTrueName />
						</fmt:param>
					</fmt:message>
				</li>
			</vwb:UserCheck>
			<vwb:UserCheck status="notAuthenticated">
				<vwb:CheckRequestContext context='!login'>
					<vwb:Permission permission="login">
						<li>
							<a href="<vwb:Link context="plain" jsp="login" format='url'/>"
								class="action login"
								title="<fmt:message key='actions.login.title'/>"><fmt:message
									key="actions.login" /> </a>
						</li>
						<li>
							<a href="<vwb:RegistLink/>" target="_blank"><fmt:message
									key="actions.register" /> </a>
						</li>
					</vwb:Permission>
				</vwb:CheckRequestContext>
			</vwb:UserCheck>
			<vwb:UserCheck status="authenticated">
			    <li>
					<a href="<vwb:Link context="plain" jsp="logout" format='url'/>"
						class="action logout"
						title="<fmt:message key='actions.logout.title'/>"><fmt:message
							key="actions.logout" /> </a>
				</li>
				<li>
					<a href="<vwb:MyVOLink/>" target="_blank"><fmt:message
							key="actions.myaccount" /> </a>
				</li>
				<vwb:CheckRequestContext context='!prefs'>
					<vwb:CheckRequestContext context='!preview'>
						<li>
							<a href="<vwb:Link page="5015" format="url"/>"
								class="action prefs" accesskey="p"
								title="<fmt:message key='actions.prefs.title'/>"><fmt:message
									key="actions.prefs" /> </a>
						</li>
					</vwb:CheckRequestContext>
				</vwb:CheckRequestContext>
				<vwb:modeCheck status="Full">
					<vwb:CheckRequestContext context="view|portlet">
					<li>
						<a
							href="<vwb:Link format="url"><vwb:Param name="m">0</vwb:Param></vwb:Link>"
							title="<fmt:message key='actions.viewmode.title'/>"><fmt:message
								key="actions.viewmode" /> </a>
					</li>
					</vwb:CheckRequestContext>
				</vwb:modeCheck>
				<vwb:modeCheck status="View">
					<vwb:CheckRequestContext context="view|portlet">
						<li>
							<a
								href="<vwb:Link format="url"><vwb:Param name="m">1</vwb:Param></vwb:Link>"
								title="<fmt:message key='actions.editmode.title'/>"><fmt:message
									key="actions.editmode" /> </a>
						</li>
					</vwb:CheckRequestContext>
				</vwb:modeCheck>
				<li>
					<a href="http://duckling.escience.cn/dct/Wiki.jsp?page=DCTHelp"
						target="_blank"><fmt:message key="actions.help" /> </a>
				</li>
			</vwb:UserCheck>
			<li id="FullScrLI">
				<a id="FullScrA" class="DCT_FullScrA"
					onclick="changeFullScreen('center_right',cancelfullscrbutton,fullscrbutton);">
					<fmt:message>public.fullscreen</fmt:message> </a>
			</li>
			<li id="langtirgger" onmouseover="showmenu()" onmouseout="hidemenu()">
				<a><fmt:message key="title.language" /> </a>
			</li>
		</ul>
	</div>
</div>

<div style="display:none" class="language DCT_hideoutmenu" id="language" >
	<ul>
		<li onmouseover="showmenu()" onmouseout="hidemenu()">
			<a href="#"
				onClick="change_locale('zh_CN','${contextPath}')">中文</a><a
				href="#"
				onClick="change_locale('en_US','${contextPath}')">English</a>
		</li>
	</ul>
</div>
