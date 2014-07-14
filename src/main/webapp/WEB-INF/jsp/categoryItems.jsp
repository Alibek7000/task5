<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tld/myTagLib.tld" prefix="myTag" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="text"/>
<html>
<head>
    <title>BikeShop - <fmt:message key="catalog"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
</head>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<body>
<div id="sideBlock">
    <jsp:include page="/WEB-INF/jsp/sideBlock.jsp"/>
</div>
<c:set var="sortingUp" value="${param.sortingUp}"/>
<div id="main">
    <div id="bigText">
        <c:choose>
            <c:when test="${not empty sessionScope.categoryItems}">
                <c:if test="${language == 'ru_RU'}"><h3><c:out value="${sessionScope.categoryRuName}"/></h3></c:if>
                <c:if test="${language == 'en_US'}"><h3><c:out value="${sessionScope.categoryName}"/></h3></c:if>
                <table>
                    <tr>
                        <th colspan="2"><fmt:message key="table.item"/>
                            <c:set var="sortingByName" value="${param.sortingByName}"/>
                            <a href="controller?action=showCategoryItems&categoryId=${sessionScope.categoryId}&parentId=${sessionScope.parentId}&page=${currentPage}&sortingByName=true&sortingByPrice=false&sortingUp=false"
                               onclick="${sortingByName}='true'" style="text-decoration: none;">↑</a>
                            <a href="controller?action=showCategoryItems&categoryId=${sessionScope.categoryId}&parentId=${sessionScope.parentId}&page=${currentPage}&sortingByName=true&sortingByPrice=false&sortingUp=true"
                               onclick="${sortingByName}='false'" style="text-decoration: none;">↓</a>
                        </th>
                        <th><fmt:message key="table.price"/>
                            <c:set var="sortingByPrice" value="${param.sortingByPrice}"/>
                            <a href="controller?action=showCategoryItems&categoryId=${sessionScope.categoryId}&parentId=${sessionScope.parentId}&page=${currentPage}&sortingByName=false&sortingByPrice=true&sortingUp=true"
                               onclick="${sortingUp}='true'" style="text-decoration: none;">↑</a>
                            <a href="controller?action=showCategoryItems&categoryId=${sessionScope.categoryId}&parentId=${sessionScope.parentId}&page=${currentPage}&sortingByName=false&sortingByPrice=true&sortingUp=false"
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

                        <c:forEach items="${sessionScope.categoryItems}" var="entry">
                        <c:set var="item" value="${entry.key}"/>
                        <td><a href="controller?action=aboutItem&id=${item.getId()}"><img
                                src="imageController?pathToImage=${sessionScope.pathToImages}/${item.getId()}.png"
                                width="150px"/></a></td>
                        <td><a href="controller?action=aboutItem&id=${item.getId()}"><c:out
                                value="${item.getName()}"/></a></td>
                        <fmt:formatNumber var="price" value="${item.getPrice()}"
                                          maxFractionDigits="2"/>
                        <td><strong>$<c:out value="${price}"/></strong></td>
                        <td align="right"><c:out value="${entry.value}"/></td>
                        <c:if test="${sessionScope.user.getClass().getSimpleName() == 'Administrator'}">
                            <td>
                                <button onClick="location.href='controller?action=showEditItemPage&id=${item.getId()}'">
                                    <fmt:message key="table.edit"/>
                                </button>
                            </td>
                            <td>
                                <button onClick="location.href='controller?action=removeItemFromBase&id=${item.getId()}'">
                                    <fmt:message key="table.remove"/>
                                </button>
                            </td>
                        </c:if>

                    </tr>
                    </c:forEach>
                </table>
                <c:if test="${noOfPages != 1}">
                    <c:if test="${currentPage != 1}">
                        <a href="controller?action=showCategoryItems&categoryId=${sessionScope.categoryId}&parentId=${sessionScope.parentId}&page=${currentPage - 1}&sortingByPrice=${sortingByPrice}&sortingByName=${sortingByName}&sortingUp=${sortingUp}"><<<</a>
                    </c:if>
                    <c:forEach begin="1" end="${noOfPages}" var="i">
                        <c:choose>
                            <c:when test="${currentPage eq i}">
                                <td>${i}</td>
                            </c:when>
                            <c:otherwise>
                                <td>
                                    <a href="controller?action=showCategoryItems&categoryId=${sessionScope.categoryId}&parentId=${sessionScope.parentId}&page=${i}&sortingByPrice=${sortingByPrice}&sortingByName=${sortingByName}&sortingUp=${sortingUp}">${i}&nbsp</a>
                                </td>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:if test="${currentPage lt noOfPages}">
                        <a href="controller?action=showCategoryItems&categoryId=${sessionScope.categoryId}&parentId=${sessionScope.parentId}&page=${currentPage + 1}&sortingByPrice=${sortingByPrice}&sortingByName=${sortingByName}&sortingUp=${sortingUp}">>>></a>
                    </c:if>
                </c:if>
            </c:when>
            <c:otherwise>
                <fmt:message key="categoryIsEmpty"/>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
