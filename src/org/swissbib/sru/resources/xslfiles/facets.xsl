<?xml version='1.0'?>

<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	 xmlns:srw="http://www.loc.gov/zing/srw/"
     xmlns:facets="urn:oclc-srw:facets"
     xmlns:ed="http://oclc.org/srw/extraData"
     xmlns:lemma="info:srw/extension/8/lemmatize"
     xmlns:spelling="info:srw/extension/7/spellCheck"
     xmlns:facetedQuery="info:srw/extension/6/facetedSearch">

<!-- Facets Extra Response Data -->

<xsl:template match="srw:extraResponseData/ed:extraData/facets:facets">
	<div>
		<div style="font-size: 1.5em;">Facets</div>
		<xsl:apply-templates select="facets:facet"/>
	</div>
</xsl:template>

<xsl:template match="facets:facets/facets:facet">
	<div style="font-size: 1.3em">
		Facet: <xsl:value-of select="facets:facetType" />
	</div>
	<table style="border: 1px solid black; width: 100%;">
		<tr>
			<th>Value</th>
			<th>Count</th>
			<th>Queries</th>
		</tr>
		<xsl:apply-templates select="facets:facetValue" />
	</table>
</xsl:template>

<xsl:template match="facets:facets/facets:facet/facets:facetValue">
    <xsl:variable name="startRecord" select="number(/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:startRecord)"/>
    <xsl:variable name="resultSetTTL">
        <xsl:if test="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:resultSetTTL">
            <xsl:text>&amp;resultSetTTL=</xsl:text><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:resultSetTTL"/>
        </xsl:if>
    </xsl:variable>

    <xsl:variable name="recordPacking">
        <xsl:text>&amp;recordPacking=</xsl:text><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:recordPacking"/>
    </xsl:variable>

    <xsl:variable name="numRecs">
        <xsl:value-of select="number(/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:maximumRecords)"/>
    </xsl:variable>

    <xsl:variable name="maximumRecords">
        <xsl:text>&amp;maximumRecords=</xsl:text><xsl:value-of select="$numRecs"/>
    </xsl:variable>

    <xsl:variable name="recordSchema">
        <xsl:if test="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:recordSchema">&amp;recordSchema=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:recordSchema"/>
        </xsl:if>
    </xsl:variable>

    <xsl:variable name="sortKeys">
        <xsl:if test="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:sortKeys">&amp;sortKeys=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:sortKeys"/>
        </xsl:if>
    </xsl:variable>

    <xsl:variable name="query">
        <xsl:text>&amp;query=</xsl:text><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query"/>
    </xsl:variable>

    <xsl:variable name="facetQuery">
        <xsl:text>&amp;query=</xsl:text><xsl:value-of select="facets:queries/facets:query/facets:queryString"/>
    </xsl:variable>

    <xsl:variable name="facetedSearch">
        <xsl:text>&amp;x-info-6-facets=</xsl:text><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:extraRequestData/facetedQuery:facetedSearch"/>
    </xsl:variable>

    <xsl:variable name="spellings">
        <xsl:text>&amp;x-info-7-spelling=</xsl:text><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:extraRequestData/spelling:spellCheck"/>
    </xsl:variable>

    <xsl:variable name="lemmatize">
        <xsl:text>&amp;x-info-8-lemmatize=</xsl:text><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:extraRequestData/lemma:lemmatizeSearch"/>
    </xsl:variable>

    <tr>
		<td>
            <a href="?operation=searchRetrieve{$facetQuery}{$maximumRecords}{$resultSetTTL}{$recordSchema}{$sortKeys}{$recordPacking}&amp;startRecord={$startRecord}{$facetedSearch}{$spellings}{$lemmatize}">
                <xsl:value-of select="facets:valueString" />
            </a>
        </td>
		<td><xsl:value-of select="facets:count" /></td>
		<td>
			<xsl:apply-templates select="facets:queries/facets:query" />
		</td>
	</tr>
</xsl:template>

<xsl:template match="facets:facets/facets:facet/facets:facetValue/facets:queries/facets:query">
	<div>
       <xsl:value-of select="facets:queryType" /> : <xsl:value-of select="facets:queryString" />
	</div>
</xsl:template>

</xsl:stylesheet>

