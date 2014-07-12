<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tld/myTagLib.tld" prefix="myTag" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="text"/>
<html>
<head>
    <title>BikeShop</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<jsp:include page="/WEB-INF/header.jsp"/>
<body>
<div id="sideBlock">
    <jsp:include page="/WEB-INF/sideBlock.jsp"/>
</div>
<div id="main">
    <div id="bigText">
        <c:if test="${not empty param.errorMessage}">
            <fmt:message key="${param.errorMessage}"/>
        </c:if><br>

        <form action="controller" name="loginForm" method="post">
            <input type="hidden" name="action" value="login">
            <fmt:message key="registration.login"/>
            <br>
            <input type="text" name="login"><br>
            <fmt:message key="registration.password"/>
            <br>
            <input type="password" name="password"><br>
            <button name="send"><fmt:message key="header.signIn"/>
            </button>
        </form>
    </div>
</div>
</body>
</html>
