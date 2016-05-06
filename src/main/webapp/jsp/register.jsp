<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="user.register"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<style>
* {
	margin: 0px;
	padding: 0px;
	font-family:Arial,Helvetica,sans-serif;
}
.pcsp_body{
	font-family: Arial, Helvetica, sans-serif;
	margin:auto;
	width: 95%;
	font-size: 12px;
}

.pcsp_panel, .csp_panel{
	background:url("<%=basePath%>images/pcsp_title_bg.gif") repeat-x scroll 0 0 #EFEFEF;
	border:1px solid #C6C6C6;
	height:20px;
	padding:4px 9px;
	word-wrap:break-word; 
	overflow:hidden;
	font-weight:bold;
	margin:auto;
}
.pcsp_panel h3{
	float:left;
	height:20px;
	margin-right:5px;
	overflow:hidden;
	font-size:14px;
	font-weight:bold;
}
.pcsp_panel .userinfo, .pcsp_panel .handle{
	float: right;
	font-size: 12px;
	font-weight: normal;
	height:16px;
	line-height:16px;
	/*
	overflow:hidden;
	*/
	/* padding-top:2px; */
	padding-right: 10px;
}
.pcsp_required{
	color:#CC0000;
}
.pcsp_panel .userinfo, .pcsp_panel .handle{
	float: right;
	font-size: 12px;
	font-weight: normal;
	height:16px;
	line-height:16px;
	/*
	overflow:hidden;
	*/
	/* padding-top:2px; */
	padding-right: 10px;
}
.pcsp_panel_body_personinfo{
	border-color:-moz-use-text-color #C6C6C6 #C6C6C6;
	height:100%;
	/* padding:9px; */
	padding: 9px;
	display:block;
	margin:auto;
}

.pcsp_panel_body_personinfo .pcsp_table td{
	border-bottom: none;
}

.pcsp_panel_body_personinfo .pcsp_table input.domain{
	width: 150px;
}
.checking{
	background:url("<%=basePath%>images/loading.gif") no-repeat scroll 0 0 transparent;
	padding-bottom:2px;
	padding-left:16px;
}
.check_success{
	background:url("<%=basePath%>images/icon_success.gif") no-repeat scroll 0 0 transparent;
	color:#EA5200;
	font-weight:bold;
	padding-bottom:2px;
	padding-left:16px;
}
.check_fail{
	background:url("<%=basePath%>/images/icon_fail.gif") no-repeat scroll 0 0 transparent;
	color:#EA5200;
	font-weight:bold;
	padding-bottom:2px;
	padding-left:16px;
}
.pcsp_table td{
	border-bottom: 1px solid #DDDDDD;
	padding: 3px 5px;
}
.pcsp_submit_panel{
	border-top:1px solid #C6C6C6;
	padding: 9px;
	display:block;
	margin:15px 0px auto;
}

.pcsp_submit_panel input{
	float: left;
	padding: 0px 10px;
}
input[type=submit],input[type=button],input[type=reset]{
	padding:2px 25px;
	/*for ie7
	*padding:2px 25px;
	*/
	font-weight: bold;
}
</style>
<c:choose>
<c:when test="${userState!=null&&!userState.currentVO}">
   <c:if test="${userState.pending}">
     <p class="pcsp_required" align="center">
         <fmt:message key="user.waiting.approval.tip">
              <fmt:param>${userState.name}</fmt:param>
         </fmt:message>
     </p>
        
   </c:if>
   <c:if test="${!userState.pending}">
    <p class="pcsp_required" align="center">
         <fmt:message key="user.noneed.register.apply.tip">
              <fmt:param>${userState.name}</fmt:param>
         </fmt:message>
     </p>
          
        <form id="registerForm" name="registerForm" action="<vwb:Link page="5029?method=applayRegister" format="url"/>" method="post">
	        <div class="pcsp_submit_panel">
						<input id="submitRegister" name="submitRegister" type="submit"
							value='<fmt:message key="user.register.apply"/>' />
		    </div>
        </form>
   </c:if>
</c:when>
<c:when test="${userState!=null&&userState.currentVO}">
     <p class="pcsp_required" align="center">
	     <fmt:message key="user.vomember.tip">
	              <fmt:param>${userState.name}</fmt:param>
	     </fmt:message>
     </p>
</c:when>
<c:otherwise>
		<script type="text/javascript">
		    //1 email 为空，2 email已经存在 4 用户名为空 8 密码为空 16 两次密码输入不匹配 32 无效的email
		    
		    var validateRegisterFlag = 1+4+8+16;
			$(document).ready(function(){
		    	$("input[name='email']").focus();
		   		//check email pattern
				isValidEmailAddress=function(emailAddress){
					var pattern = new RegExp(/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i);
					return pattern.test(emailAddress);
				};
				
		    	$("input[name='email']").blur(function(){
						var email=$(this).val();
						if(email.trim()=="")
						{
						   validateRegisterFlag = validateRegisterFlag|1;
						   validateRegisterFlag = validateRegisterFlag&2?validateRegisterFlag^2:validateRegisterFlag;
						
						   return;
						}else
						{
						   validateRegisterFlag = validateRegisterFlag&1?validateRegisterFlag^1:validateRegisterFlag;
						}
						var isValidEmail=isValidEmailAddress(email);
						if(isValidEmail){
					        validateRegisterFlag = validateRegisterFlag&32?validateRegisterFlag^32:validateRegisterFlag;
							$.ajax({
								url:"<vwb:Link page="5029?method=validateDupRegister" format="url"/>",
								type:"POST",
								data:"email="+email,
								dataType:"json",
								beforeSend:function(){
									$("#valUserCnt").html("<label class='checking'>&nbsp;</label>");
								},
								error:function(data){},
								complete:function(){},
								success:function(data){
									var result=data.result;
									if(result=='ok'){
									    validateRegisterFlag = validateRegisterFlag&2?validateRegisterFlag^2:validateRegisterFlag;
										$("#valUserCnt").html("<label class='check_success'> </label>");
									}else if(result=='userExists'){
									    validateRegisterFlag = validateRegisterFlag|2;
										$("#valUserCnt").html('<label class="check_fail"><fmt:message key="user.haspassport.tip"><fmt:param><vwb:Link context="plain" jsp="login" format='url'/></fmt:param></fmt:message></label>');
									}
								}
							});
						}else
						{
						    validateRegisterFlag = validateRegisterFlag|32;
					    	$("#valUserCnt").html('<label class="check_fail"><fmt:message key="user.email.invalid.tip"/></label>');
						}
				});
				$("#pass").blur(function(){
				    var pass=$(this).val();
				    if(pass.trim()=="")
					{
					   validateRegisterFlag = validateRegisterFlag|8;
					}else
					{
					   validateRegisterFlag = validateRegisterFlag&8?validateRegisterFlag^8:validateRegisterFlag;
					}
				});
				$("#confirmPass").blur(function(){
				    var confirmPass=$(this).val();
				    var passValue = $("#pass").val().trim();
				    if(passValue == "")
				    {
				       return;
				    }
				    if(confirmPass.trim()==passValue)
					{
					   validateRegisterFlag = validateRegisterFlag&16?validateRegisterFlag^16:validateRegisterFlag;
					    
					}else
					{
					   validateRegisterFlag = validateRegisterFlag|16;
					}
				});
				$("#trueName").blur(function(){
				    var pass=$(this).val();
				    if(pass.trim()=="")
					{
					   validateRegisterFlag = validateRegisterFlag|4;
					}else
					{
					   validateRegisterFlag = validateRegisterFlag&4?validateRegisterFlag^4:validateRegisterFlag;
					}
				});
				$("#registerForm").submit(function(){
				   var flag = true;
				   if(validateRegisterFlag&1)
				   {
				      $("#valUserCnt").html('<label class="check_fail"><fmt:message key="user.email.required.tip"/></label>');
			          flag = false;
				   }
				   if(validateRegisterFlag&2)
				   {
				      $("#valUserCnt").html('<label class="check_fail"><fmt:message key="user.haspassport.tip"><fmt:param><vwb:Link context="plain" jsp="login" format='url'/></fmt:param></fmt:message></label>');
			          flag = false;
				   }
				   if(validateRegisterFlag&32)
				   {
				      $("#valUserCnt").html('<label class="check_fail"><fmt:message key="user.email.invalid.tip"/></label>');
			          flag = false;
				   }
				   if(validateRegisterFlag&4)
				   {
				      $("#trueNameCnt").html('<label class="check_fail"><fmt:message key="user.uesrname.required.tip"/></label>');
			          flag = false;
				   }else
				   {
				     $("#trueNameCnt").html('');
				   }
				   if(validateRegisterFlag&8)
				   {
				      $("#passCnt").html('<label class="check_fail"><fmt:message key="user.password.required.tip"/></label>');
			          flag = false;
				   }else
				   {
				     $("#passCnt").html('');
				   }
				   if(validateRegisterFlag&16)
				   {
				      if($("#pass").val().trim()!="")
				      {
				        $("#confirmPassCnt").html('<label class="check_fail"><fmt:message key="user.passoword.again.tip"/></label>');
				      }
				      flag = false;
				   }else
				   {
				     $("#confirmPassCnt").html('');
				   }
				   return flag;
				});	 
				
			});
		</script>
		<form id="registerForm" name="registerForm" action="<vwb:Link page="5029?method=saveRegister" format="url"/>"  method="post">
		  <div id="regCnt" class="pcsp_body">
		     <p class="pcsp_required">
		     <fmt:message key="user.register.top"/>
		     </p>
		     <div class="pcsp_panel">
					<h3><fmt:message key="user.personal.info"/></h3>
					<div class="userinfo">
					  <fmt:message key="user.haspassport.login">
					     <fmt:param><vwb:Link context="plain" jsp="login" format='url'/></fmt:param>
					  </fmt:message>
					</div>
			</div>
			<div class="pcsp_panel_body_personinfo">
					<p>
						<b class="pcsp_required">
						<fmt:message key="user.required.flag"/>
						</b>
					</p>
					<table class="pcsp_table">
					    <tr>
							<td class="pcsp_firstcol">
							<fmt:message key="user.email"/>
								<span class="pcsp_required">*</span>
							</td>
							<td>
							  <c:choose>
								<c:when test="${isLogin=='true'}">
								        <input type="text" name="email" value="admin@root.umt" readonly />
								    </c:when>
								<c:otherwise>
								         <input type="text" name="email" value="" /> 
						        </c:otherwise>
				               </c:choose>
								<span id="valUserCnt"></span>
								<br/>
								<small><fmt:message key="user.email.passport.tip"/></small>
							</td>
						</tr>
						<tr>
								<td class="pcsp_firstcol">
								<fmt:message key="user.username"/>
									<span class="pcsp_required">*</span>
								</td>
								<td>
									<input id="trueName" type="text" name="trueName" /> <span id="trueNameCnt"></span>
								</td>
						</tr>
						<c:if test="${isLogin!='false'}">
							<tr>
								<td class="pcsp_firstcol">
								<fmt:message key="user.password"/>
									
									<span class="pcsp_required">*</span>
								</td>
								<td>
									<input id="pass" type="password" name="pass" /> <span id="passCnt"></span>
								</td>
							</tr>
							<tr>
								<td class="pcsp_firstcol">
								<fmt:message key="user.password.again"/>
									<span class="pcsp_required">*</span>
								</td>
								<td>
									<input id="confirmPass" type="password" name="confirmPass" /><span id="confirmPassCnt"></span>
								</td>
							</tr>
						</c:if>
						
					</table>
			</div>
			<div class="pcsp_submit_panel">
					<input id="submitRegister" name="submitRegister" type="submit"
						value='<fmt:message key="user.register.submit"/>' />
			</div>
			<br>
		  </div>
		</form>
  </c:otherwise>  
</c:choose>
