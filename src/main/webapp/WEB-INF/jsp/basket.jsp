<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/tld/myTagLib.tld" prefix="myTag" %>

<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="text"/>
<html>
<head>
    <title>BikeShop - <fmt:message key="header.basket"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<jsp:include page="/WEB-INF/jsp/header.jsp.jsp"/>
<body>
<div id="sideBlock">
    <jsp:include page="/WEB-INF/jsp/sideBlock.jsp.jsp"/>
</div>
<div id="main">
    <div id="bigText">
        <c:choose>
            <c:when test="${sessionScope.totalSum > 0}">
                <table>
                    <tr>
                        <th>Id</th>
                        <th colspan="2" bgcolor="#D6D6D6"><fmt:message key="table.item"/></th>
                        <th bgcolor="#D6D6D6"><fmt:message key="table.price"/></th>
                        <th bgcolor="#D6D6D6"><fmt:message key="quantity"/></th>
                        <th bgcolor="#D6D6D6"><fmt:message key="table.remove"/></th>
                    </tr>
                    <c:forEach items="${sessionScope.items}" var="entry">
                        <tr>
                            <c:set var="item" value="${entry.key}"/>
                            <td><c:out value="${item.getId()}"/></td>
                            <td><a href="controller?action=aboutItem&id=${item.getId()}"><img
                                    src="images/items/${item.getId()}.png"
                                    width="150px"/></a></td>
                            <td><a href="controller?action=aboutItem&id=${item.getId()}"><c:out
                                    value="${item.getName()}"/></a>
                            </td>
                            <fmt:formatNumber var="price" value="${item.getPrice()}"
                                              maxFractionDigits="2"/>
                            <td><strong>$<c:out value="${price}"/></strong></td>
                            <td align="right"><c:out value="${entry.value}"/></td>
                            <td>
                                <button onClick="location.href='controller?action=removeItem&id=${item.getId()}'">
                                    <fmt:message key="table.remove"/>
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                <br>
                <fmt:formatNumber var="total" value="${sessionScope.totalSum}"
                                  maxFractionDigits="2"/>
                <fmt:message key="basket.total"/> <strong>$<c:out value="${total}"/></strong>
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <c:if test="${sessionScope.user.getClass().getSimpleName() == 'Client'}">
                            <c:choose>
                                <c:when test="${sessionScope.user.isBanned() == 'true'}">
                                    <br><fmt:message key="error.banned"/>
                                </c:when>
                                <c:otherwise>
                                    <button onClick="location.href='controller?action=pay'"><fmt:message
                                            key="basket.pay"/></button>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </c:when>
                    <c:otherwise><br><fmt:message key="basket.logInToBuy"/>
                        <br><a href="controller?action=registration"><fmt:message key="header.registration"/></a>
                        <br> <fmt:message key="header.or"/>
                        <br>
                        <a href="controller?action=login"><fmt:message key="header.signIn"/></a>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <fmt:message key="basket.basketIsEmpty"/>
            </c:otherwise>
        </c:choose>
        <c:if test="${not empty param.errorMessage}">
            <fmt:message key="${param.errorMessage}"/>
        </c:if>
    </div>
</div>
</body>
</html>
