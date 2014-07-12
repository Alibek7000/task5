<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="text"/>

<html lang="${language}">
<head>
    <link rel="stylesheet" type="text/css" href="../css/style.css">
</head>
<header>
    <a href="${pageContext.request.contextPath}/">
        <div id="logo"></div>
    </a>
</header>

<h4>

    <a href="${pageContext.request.contextPath}/"><fmt:message key="header.shopAll"/></a>
    <c:if test="${sessionScope.user.getClass().getSimpleName() == 'Administrator'}">
        <a href="controller?action=showAdminPanel"><fmt:message key="header.admin"/></a>
    </c:if>
    <c:choose>
        <c:when test="${empty sessionScope.user}">
            <a href="controller?action=registration"><fmt:message key="header.registration"/></a>
            <fmt:message key="header.or"/> <a href="controller?action=login"><fmt:message
                key="header.signIn"/></a>
        </c:when>
        <c:otherwise>
            <a href="controller?action=logOut"><fmt:message key="header.logOut"/></a>
        </c:otherwise>
    </c:choose>

    <a href="controller?action=showBasket"><fmt:message key="header.basket"/>
        <c:choose>
            <c:when test="${sessionScope.totalSum > 0}"><img src="../images/nonEmptyBasket.png" width="16px"
                                                             style="display: inline-table;">
            </c:when>
            <c:otherwise>
                <img src="../images/emptyBasket.png" width="16px" style="display: inline-table;">
            </c:otherwise>
        </c:choose>
    </a>

    <form action="controller?action=setLanguage" method="post" style="display: inline;">
        <select id="language" name="language" onchange="submit()">
            <option selected="selected">
                <c:if test="${language == 'ru_RU'}">
                RU
            <option value="en" ${language == 'en' ? 'selected' : ''}>EN</option>
            </c:if>
            <c:if test="${language == 'en_US'}">
                EN
                <option value="ru" ${language == 'ru' ? 'selected' : ''}>RU</option>
            </c:if>
        </select>

    </form>

</h4>

</html>
