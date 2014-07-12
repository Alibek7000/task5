<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<div id="sideBlock">
    <jsp:include page="/WEB-INF/jsp/sideBlock.jsp"/>
</div>

<div id="main">
    <div id="bigText">
        <table>
            <tr>
                <th colspan="2"><fmt:message key="table.item"/></th>
                <th><fmt:message key="table.price"/>
                    <c:set var="sortingUp" value="${param.sortingUp}"/>
                    <a href="controller?action=showItems&page=${currentPage}&sortingUp=true"
                       onclick="${sortingUp}='true'" style="text-decoration: none;">↑</a>
                    <a href="controller?action=showItems&page=${currentPage}&sortingUp=false"
                       onclick="${sortingUp}='false'" style="text-decoration: none;">↓</a>
                </th>
                <th><fmt:message key="table.available"/></th>
                <c:if test="${sessionScope.user.getClass().getSimpleName() == 'Administrator'}">
                    <th><fmt:message key="table.edit"/></th>
                    <th><fmt:message key="table.remove"/></th>
                </c:if>
            </tr>
            <tr>
                <td><a href="controller?action=showCategoryItems&categoryId=${category.getId()}"><c:out
                        value="${category.getName()}"/></a></td>
            <tr>

                <c:forEach items="${sessionScope.availableItems}" var="entry">
                <c:set var="item" value="${entry.key}"/>
                <td><a href="controller?action=aboutItem&id=${item.getId()}"><img src="images/items/${item.getId()}.png"
                                                                                  width="150px"/></a></td>
                <td><a href="controller?action=aboutItem&id=${item.getId()}"><c:out value="${item.getName()}"/></a></td>
                <fmt:formatNumber var="price" value="${item.getPrice()}"
                                  maxFractionDigits="2"/>
                <td><strong>$<c:out value="${price}"/></strong></td>
                <td align="right"><c:out value="${entry.value}"/></td>
                <c:if test="${sessionScope.user.getClass().getSimpleName() == 'Administrator'}">
                    <td>
                        <button onClick="location.href='controller?action=showEditItemPage&id=${item.getId()}'">
                            <fmt:message
                                    key="table.edit"/>
                        </button>
                    </td>
                    <td>
                        <button onClick="location.href='controller?action=removeItemFromBase&id=${item.getId()}'">
                            <fmt:message
                                    key="table.remove"/>
                        </button>
                    </td>
                </c:if>

            </tr>
            </c:forEach>
        </table>
        <c:if test="${currentPage != 1}">
            <a href="controller?action=showItems&page=${currentPage - 1}&sortingUp=${sortingUp}"><<<</a>
        </c:if>
        <c:forEach begin="1" end="${noOfPages}" var="i">
            <c:choose>
                <c:when test="${currentPage eq i}">
                    <td>${i}</td>
                </c:when>
                <c:otherwise>
                    <td><a href="controller?action=showItems&page=${i}&sortingUp=${sortingUp}">${i}&nbsp</a></td>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <c:if test="${currentPage lt noOfPages}">
            <a href="controller?action=showItems&page=${currentPage + 1}&sortingUp=${sortingUp}">>>></a>
        </c:if>

        <c:if test="${sessionScope.user.getClass().getSimpleName() == 'Administrator'}">
            <button onClick="location.href='controller?action=addItem'"><fmt:message key="button.add"/></button>
        </c:if>
    </div>
</div>
</body>
</html>


