<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:marc="http://www.loc.gov/MARC21/slim" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" exclude-result-prefixes="marc">
	<xsl:import href="MARC21slimUtils.nonamespace.xsl"/>
	<xsl:output method="xml" indent="yes"/>
	<!--
	Fixed 530 Removed type="original" from dc:relation 2010-11-19 tmee
	Fixed 500 fields. 2006-12-11 ntra
	Added ISBN and deleted attributes 6/04 jer
	-->
	<xsl:template match="/">
		<xsl:if test="collection">
			<oai_dc:dcCollection xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
				<xsl:for-each select="collection">
					<xsl:for-each select="record">
						<oai_dc:dc>
							<xsl:apply-templates select="."/>
						</oai_dc:dc>
					</xsl:for-each>
				</xsl:for-each>
			</oai_dc:dcCollection>
		</xsl:if>
		<xsl:if test="record">
				<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="record">

        <xsl:variable name="controlField001" select="controlfield[@tag=001]"/>
        <xsl:variable name="controlField008" select="controlfield[@tag=008]"/>
        <xsl:variable name="dataField856" select="datafield[@tag=856 and @ind1=1]"/>
        <xsl:variable name="beginDate008" select="substring($controlField008,8,4)"/>
        <xsl:variable name="endDate008" select="substring($controlField008,12,4)"/>

        <xsl:variable name="beginDate046" select="datafield[@tag=046]/subfield[@code='c']"/>
        <xsl:variable name="endDate046" select="datafield[@tag=046]/subfield[@code='e']"/>

        <xsl:variable name="dataField593" select="datafield[@tag=593]"/>
        <xsl:variable name="divider593" select="'-'"/>

        <xsl:variable name="beginDate593" select="translate(substring-before($dataField593,$divider593),'.','-')"/>
        <xsl:variable name="endDate593" select="translate(substring-after($dataField593,$divider593),'.','-')"/>

        <recordData>

		<isad:identity xmlns:isad="http://www.expertisecentrumdavid.be/xmlschemas/isad.xsd">

			<isad:reference xmlns:isad="http://www.expertisecentrumdavid.be/xmlschemas/isad.xsd">
                <xsl:value-of select="datafield[@tag=949]/subfield[@code='j']"/>
			</isad:reference>

            <xsl:for-each select="datafield[@tag=245]">
            <isad:title xmlns:isad="http://www.expertisecentrumdavid.be/xmlschemas/isad.xsd">
				<xsl:call-template name="subfieldSelect">
                    <xsl:with-param name="codes">ab</xsl:with-param>
					<xsl:with-param name="delimeter">' : '</xsl:with-param>
				</xsl:call-template>
            </isad:title>
		    </xsl:for-each>

            <xsl:for-each select="datafield[@tag=260]">
                <isad:date xmlns:isad="http://www.expertisecentrumdavid.be/xmlschemas/isad.xsd">
                    <xsl:call-template name="subfieldSelect">
                        <xsl:with-param name="codes">c</xsl:with-param>
                    </xsl:call-template>
                </isad:date>
            </xsl:for-each>

            <xsl:for-each select="datafield[@tag=351]">
			    <isad:descriptionlevel xmlns:isad="http://www.expertisecentrumdavid.be/xmlschemas/isad.xsd">
                    <xsl:call-template name="subfieldSelect">
                        <xsl:with-param name="codes">c</xsl:with-param>
                    </xsl:call-template>
			    </isad:descriptionlevel>
            </xsl:for-each>

            <xsl:for-each select="datafield[@tag=300]">
			    <isad:extent xmlns:isad="http://www.expertisecentrumdavid.be/xmlschemas/isad.xsd">
                    <xsl:call-template name="subfieldSelect">
                        <xsl:with-param name="codes">a</xsl:with-param>
                    </xsl:call-template>
			    </isad:extent>
            </xsl:for-each>

		</isad:identity>

		<isad:context xmlns:isad="http://www.expertisecentrumdavid.be/xmlschemas/isad.xsd">

            <xsl:for-each select="datafield[@tag=100]|datafield[@tag=700]">
            <isad:creator xmlns:isad="http://www.expertisecentrumdavid.be/xmlschemas/isad.xsd">
                <xsl:call-template name="subfieldSelect">
                    <xsl:with-param name="codes">a</xsl:with-param>
                </xsl:call-template>
                <xsl:for-each select="subfield">
                    <xsl:if test="@code='b'">
                         <xsl:value-of select="concat(' ', text())"/>
                    </xsl:if>
                    <xsl:if test="@code='c'">
                        <xsl:value-of select="concat(', ', text())"/>
                    </xsl:if>
                    <xsl:if test="@code='d'">
                        <xsl:value-of select="concat(' (', text(),')')"/>
                    </xsl:if>
                    <xsl:if test="@code='e'">
                        <xsl:value-of select="concat(' (', text(),')')"/>
                    </xsl:if>
                </xsl:for-each>
			</isad:creator>
		    </xsl:for-each>

            <xsl:for-each select="datafield[@tag=110]|datafield[@tag=710]">
                <isad:creator xmlns:isad="http://www.expertisecentrumdavid.be/xmlschemas/isad.xsd">
                    <xsl:call-template name="subfieldSelect">
                        <xsl:with-param name="codes">a</xsl:with-param>
                    </xsl:call-template>
                    <xsl:for-each select="subfield">
                        <xsl:if test="@code='b'">
                            <xsl:value-of select="concat(', ', text())"/>
                        </xsl:if>
                        <xsl:if test="@code='g'">
                            <xsl:value-of select="concat(' (', text(),')')"/>
                        </xsl:if>
                        <xsl:if test="@code='e'">
                            <xsl:value-of select="concat(' (', text(),')')"/>
                        </xsl:if>
                    </xsl:for-each>
                </isad:creator>
            </xsl:for-each>

            <xsl:for-each select="datafield[@tag=111]|datafield[@tag=711]">
                <isad:creator xmlns:isad="http://www.expertisecentrumdavid.be/xmlschemas/isad.xsd">
                    <xsl:call-template name="subfieldSelect">
                        <xsl:with-param name="codes">a</xsl:with-param>
                    </xsl:call-template>
                    <xsl:for-each select="subfield">
                        <xsl:if test="@code='e'">
                            <xsl:value-of select="concat('. ', text())"/>
                        </xsl:if>
                        <xsl:if test="@code='n'">
                            <xsl:value-of select="concat(', ', text())"/>
                        </xsl:if>
                        <xsl:if test="@code='d'">
                            <xsl:value-of select="concat(', ', text())"/>
                        </xsl:if>
                        <xsl:if test="@code='c'">
                            <xsl:value-of select="concat(', ', text())"/>
                        </xsl:if>
                        <xsl:if test="@code='j'">
                            <xsl:value-of select="concat(' (', text(),')')"/>
                        </xsl:if>
                    </xsl:for-each>
                </isad:creator>
            </xsl:for-each>

		</isad:context>

        </recordData>

        <extraRecordData>

            <rel:score xmlns:rel="info:srw/extension/2/relevancy-1.0/">1</rel:score>

            <ap:link xmlns:ap="http://www.archivportal.ch/srw/extension/">
                http://aleph.unibas.ch/F/?local_base=DSV05&amp;func=find-b&amp;find_code=SYS&amp;request=<xsl:value-of select="substring($controlField001,4)"/>
            </ap:link>

            <xsl:choose>
                <xsl:when test="$dataField593">
                    <beginDateISO xmlns:ap="http://www.archivportal.ch/srw/extension/">
                        <xsl:value-of select="$beginDate593"/>
                    </beginDateISO>

                    <endDateISO xmlns:ap="http://www.archivportal.ch/srw/extension/">
                        <xsl:value-of select="$endDate593"/>
                    </endDateISO>
                </xsl:when>

                <xsl:otherwise>
                    <beginDateISO xmlns:ap="http://www.archivportal.ch/srw/extension/">
                        <xsl:value-of select="$beginDate008"/>
                    </beginDateISO>

                    <endDateISO xmlns:ap="http://www.archivportal.ch/srw/extension/">
                        <xsl:value-of select="$endDate008"/>
                    </endDateISO>
                </xsl:otherwise>
            </xsl:choose>

            <endApprox xmlns:ap="http://www.archivportal.ch/srw/extension/">0</endApprox>

            <beginApprox xmlns:ap="http://www.archivportal.ch/srw/extension/">0</beginApprox>

            <hasDigitizedItems xmlns:ap="http://www.archivportal.ch/srw/extension/">
                <xsl:choose>
                    <xsl:when test="$dataField856">1</xsl:when>
                    <xsl:otherwise>0</xsl:otherwise>
                </xsl:choose>
            </hasDigitizedItems>

        </extraRecordData>

	</xsl:template>
</xsl:stylesheet>

<!-- Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="..\..\..\..\..\..\..\..\..\..\javadev4\testsets\diacriticu8.xml" htmlbaseurl="" outputurl="" processortype="internal" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
-->