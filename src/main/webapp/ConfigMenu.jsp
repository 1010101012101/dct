<%@ page language="java" pageEncoding="utf-8"%>

<%@ page errorPage="/Error.jsp"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<fmt:setBundle basename="templates.default" />
<div class="left">
	<div class="left_top">

		<div class="left_kzt_top boder_b f14px_black_b">
			<img src="${contextPath}/images/kz_img_03.gif" />
			<span class="f14px_b STYLE2" style="color:#333333;"><fmt:message
					key="userconfig.syscofig" />
			</span>
		</div>
		<vwb:IsAdminSite>
			<vwb:Permission permission="allPermission">
			<div class="vister">
				<img src="${contextPath}/images/man_07.gif" />
				<span class="f14px_White_b">
					<fmt:message key="menu.multisite.manage" />
				</span>
			</div>
			<div class="vister_ul">
				<ul>
					<li>
						<vwb:Link page="5027">
							<fmt:message key="menu.site.create" />
						</vwb:Link>
					</li>
					<li>
						<vwb:Link page="5024">
							<fmt:message key="menu.site.list" />
						</vwb:Link>
					</li>
				</ul>
			</div>
			</vwb:Permission>
			<div class="clear"></div>
		</vwb:IsAdminSite>
		<div class="vister">
			<img src="${contextPath}/images/man_07.gif" />
			<span class="f14px_White_b"><fmt:message
					key="userconfig.mycofig" />
			</span>
		</div>
		<div class="vister_ul">
			<ul>
				<li>
					<vwb:Link page="5015">
						<fmt:message key="userconfig.mypage" />
					</vwb:Link>
				</li>
				<li>
					<vwb:Link page="5011">
						<vwb:Param name="method">show</vwb:Param>
						<fmt:message key="userconfig.myorder" />
					</vwb:Link>
				</li>
				<li>
					<a href="<vwb:UMTLink/>" target="_umt">
						<fmt:message key="actions.myaccount" />
					</a>
				</li>
			</ul>
		</div>
		<div class="clear"></div>
		<vwb:Permission permission="allPermission">
			<div class="vister">
				<img src="${contextPath}/images/man_07.gif" />
				<span class="f14px_White_b"><fmt:message
						key="userconfig.siteconfig" />
				</span>
			</div>
			<div class="vister_ul">
				<ul>
					<li>
						<vwb:Link page="5006">
							<fmt:message key="userconfig.common" />
						</vwb:Link>
					</li>
					<li>
						<vwb:Link page="5026">
							<fmt:message key="menu.site.properties" />
						</vwb:Link>
					</li>	
					<li>
						<vwb:Link page="5025">
							<fmt:message key="menu.site.policy" />
						</vwb:Link>
					</li>
					<li>
						<vwb:Link page="5008">
							<vwb:Param name="method">showBanners</vwb:Param>
							<fmt:message key="userconfig.bannercustom" />
						</vwb:Link>
					</li>
					<li>
						<vwb:Link page="5016">
							<fmt:message key="userconfig.pageadmin" />
						</vwb:Link>
					</li>
					<li>
						<vwb:Link page="5002">
							<fmt:message key="userconfig.skin"/>
						</vwb:Link>
					</li>
					<li>
						<vwb:Link page="2501">
							<fmt:message key="userconfig.pluginadmin" />
						</vwb:Link>
					</li>
					<li>
						<vwb:Link page="5007">
							<fmt:message key="userconfig.Emailmanage" />
						</vwb:Link>
					</li>
				</ul>
			</div>
			<div class="clear"></div>
		</vwb:Permission>
	</div>
</div>