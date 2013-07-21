<?xml version='1.0'?>

<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:srw="http://www.loc.gov/zing/srw/"
     xmlns:lemma="info:srw/extension/8/lemmatize"
     xmlns:spelling="info:srw/extension/7/spellCheck"
     xmlns:facetedQuery="info:srw/extension/6/facetedSearch"
     xmlns:similarTo="info:srw/extension/9/similar-to"
     xmlns:getHoldings="info:srw/extension/10/get-holdings"
     xmlns:holdings="urn:oclc-srw:holdings"
     xmlns:extraData="http://oclc.org/srw/extraData">


    <xsl:import href="/xslfiles/facets.xsl"/>
    <xsl:import href="/xslfiles/spellings.xsl"/>
    <xsl:import href="/xslfiles/stdiface.xsl"/>
    <xsl:import href="/xslfiles/dublinCoreRecord.xsl"/>
    <xsl:import href="/xslfiles/MarcXmlToTaggedText.xsl"/>


    <xsl:variable name="simTitle">Similarity Search for:
        <xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:extraRequestData/similarTo:similarTo"/>
    </xsl:variable>
    <xsl:variable name="title">Results for Search:
        <xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query"/>
    </xsl:variable>


    <xsl:variable name="dbname">
        <xsl:value-of select="/srw:searchRetrieveResponse/srw:extraResponseData/databaseTitle"/>
    </xsl:variable>
    <xsl:variable name="count">
        <xsl:value-of select="/srw:searchRetrieveResponse/srw:numberOfRecords"/>
    </xsl:variable>

    <xsl:template match="/">
        <xsl:choose>
            <xsl:when test="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:extraRequestData/similarTo:similarTo">
                <xsl:call-template name="stdiface">
                    <xsl:with-param name="localTitle" select="$simTitle"/>
                    <xsl:with-param name="localDbname" select="$dbname"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="stdiface">
                    <xsl:with-param name="localTitle" select="$title"/>
                    <xsl:with-param name="localDbname" select="$dbname"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <xsl:template match="srw:searchRetrieveResponse">
        <div id="nav">
            <span class="label">Links:</span>
            <a href="?">&lt;&lt; Back to Search</a><!--span class="bullet">&bull;</span><a href="">Terminologies Home</a-->
        </div>
        <div id="content">
            <xsl:apply-templates select="srw:diagnostics"/>
            <h1>
                <xsl:choose>
                    <xsl:when test="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:extraRequestData/similarTo:similarTo">
                        <xsl:value-of select="$simTitle"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$title"/>
                    </xsl:otherwise>
                </xsl:choose>
            </h1>
            <div id="recordDisplayDetails">
                <xsl:apply-templates select="srw:numberOfRecords"/>
                <xsl:call-template name="prev-nextRecord"/>
            </div>
            <xsl:apply-templates select="srw:records"/>
			<xsl:apply-templates select="srw:extraResponseData"/>
            <div id="bottomlinks">
                <xsl:call-template name="prev-nextRecord"/>
            </div>
        </div> <!--content-->
    </xsl:template>

    <xsl:template match="srw:numberOfRecords">
  Records found:
        <xsl:text> </xsl:text>
        <xsl:value-of select="."/>
        <br/>
    </xsl:template>

    <xsl:template match="srw:records">
        <div id="recordsWrapper">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="srw:record">
        <div class="recordDesc">
            <h2 class="recordCount">Record:
                <xsl:value-of select="srw:recordPosition"/> of
                <xsl:value-of select="$count"/>
            </h2>
            <xsl:apply-templates select="child::srw:recordSchema"/>
        </div>
        <xsl:apply-templates select="child::srw:recordData"/>
        <xsl:apply-templates select="child::srw:extraRecordData"/>
    </xsl:template>

    <xsl:template match="srw:extraRecordData">
        
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
            <xsl:text>&amp;maximumRecords=</xsl:text>
            <xsl:value-of select="$numRecs"/>
        </xsl:variable>

        <xsl:variable name="recordSchema">
            <xsl:if test="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:recordSchema">&amp;recordSchema=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:recordSchema"/></xsl:if>
        </xsl:variable>

        <xsl:variable name="sortKeys">
            <xsl:if test="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:sortKeys">&amp;sortKeys=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:sortKeys"/></xsl:if>
        </xsl:variable>

        <xsl:variable name="query">
            <xsl:text>&amp;query=</xsl:text><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query"/>
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

        <xsl:variable name="similaritySearch">
            <xsl:text>&amp;x-info-9-similar-to=</xsl:text><xsl:value-of select="extraData:extraData/extraData:documentVector"/>
        </xsl:variable>

        <xsl:variable name="getHoldings">
            <xsl:text>&amp;x-info-10-get-holdings=</xsl:text><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:extraRequestData/getHoldings:getHoldings"/>
        </xsl:variable>

        <div class="similarRecordSearch">
            <a href="?operation=searchRetrieve{$query}{$maximumRecords}{$resultSetTTL}{$recordSchema}{$sortKeys}{$recordPacking}{$facetedSearch}{$spellings}{$lemmatize}{$similaritySearch}{$getHoldings}&amp;startRecord=1">
                Similar Records
            </a>
        </div>

        <xsl:apply-templates select="extraData:extraData/holdings:holdings" />

    </xsl:template>

    <xsl:template match="extraData:extraData/holdings:holdings">
        <div>
            <h3>Holdings</h3>
            <div class ="recordWrapper">
                <xsl:apply-templates select="holdings:datafield" />
            </div>
        </div>
    </xsl:template>

    <xsl:template match="holdings:datafield">
        <div class="entry">
            <div class="fixed">
                <span class="field">
                    <xsl:value-of select="@tag"/>
                </span>
                <span class="indicator">
                    <xsl:choose>
                        <xsl:when test="@ind1=' '">
                            <xsl:text>_</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="@ind1"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </span>
                <span class="indicator">
                    <xsl:choose>
                        <xsl:when test="@ind2=' '">
                            <xsl:text>_</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="@ind1"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </span>
            </div>
            <div class="subfieldset">
                <xsl:apply-templates select="holdings:subfield"/>
            </div>
        </div>
    </xsl:template>

    <xsl:template match="holdings:subfield">
        <span class="subfield">$
            <xsl:value-of select="@code"/>
        </span>
        <xsl:choose>
            <xsl:when test="@code='u'">
                <span class="data">
                    <a href="{.}">
                        <xsl:value-of select="."/>
                    </a>
                </span>
            </xsl:when>
            <xsl:otherwise>
                <span class="data">
                    <xsl:value-of select="."/>
                </span>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="srw:record/srw:recordSchema">
        <div class="recordSchema">Schema:
            <xsl:variable name="schema" select="."/>
            <xsl:choose>
                <xsl:when test="$schema = 'http://www.openarchives.org/OAI/2.0/#header'">OAI Header</xsl:when>
                <xsl:when test="$schema = 'info:srw/schema/1/dc-v1.1'">Dublin Core</xsl:when>
                <xsl:when test="$schema = 'info:srw/schema/1/marcxml-v1.1'">MARC XML</xsl:when>
                <xsl:when test="$schema = 'info:srw/schema/1/mods-v3.0'">MODS</xsl:when>
                <xsl:when test="$schema = 'http://srw.o-r-g.org/schemas/ccg/1.0/'">Collectable Card Schema</xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$schema"/>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>

    <xsl:template match="srw:recordPosition">
  Position: 
        <xsl:value-of select="."/>
        <xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template match="srw:nextRecordPosition">
  <!-- Not used -->
    </xsl:template>

    <xsl:template match="srw:recordData">
        <div class="recordWrapper">
            <xsl:choose>
                <xsl:when test="../srw:recordPacking = 'string'">
                    <pre>
                        <xsl:value-of select="."/>
                    </pre>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates/>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>


    <xsl:template name="prev-nextRecord">
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
            <xsl:text>&amp;maximumRecords=</xsl:text>
            <xsl:value-of select="$numRecs"/>
        </xsl:variable>

        <xsl:variable name="prev" select="$startRecord - $numRecs"/>

        <xsl:variable name="recordSchema">
            <xsl:if test="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:recordSchema">&amp;recordSchema=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:recordSchema"/></xsl:if>
        </xsl:variable>

        <xsl:variable name="sortKeys">
            <xsl:if test="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:sortKeys">&amp;sortKeys=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:sortKeys"/></xsl:if>
        </xsl:variable>
        
        <xsl:variable name="query">
            <xsl:text>&amp;query=</xsl:text><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query"/>
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

        <xsl:variable name="similaritySearch">
            <xsl:text>&amp;x-info-9-similar-to=</xsl:text><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:extraRequestData/similarTo:similarTo"/>
        </xsl:variable>
        
        <xsl:variable name="getHoldings">
            <xsl:text>&amp;x-info-10-get-holdings=</xsl:text><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:extraRequestData/getHoldings:getHoldings"/>
        </xsl:variable>

        <xsl:if test="$prev>0">
            <a href="?operation=searchRetrieve{$query}{$maximumRecords}{$resultSetTTL}{$recordSchema}{$sortKeys}{$recordPacking}&amp;startRecord={$prev}{$facetedSearch}{$spellings}{$lemmatize}{$similaritySearch}{$getHoldings}">
                <xsl:text>&lt;&lt;Previous Record(s)</xsl:text>
            </a>
            <xsl:text> </xsl:text>
        </xsl:if>

        <xsl:if test="/srw:searchRetrieveResponse/srw:nextRecordPosition">
            <a href="?operation=searchRetrieve{$query}{$maximumRecords}{$resultSetTTL}{$recordSchema}{$sortKeys}{$recordPacking}&amp;startRecord={/srw:searchRetrieveResponse/srw:nextRecordPosition}{$facetedSearch}{$spellings}{$lemmatize}{$similaritySearch}{$getHoldings}">
                <xsl:text>Next Record(s)&gt;&gt;</xsl:text>
            </a>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
