<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:marc="http://www.loc.gov/MARC21/slim" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" exclude-result-prefixes="marc"
	xmlns:dcterms="http://purl.org/dc/terms/">
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
			<oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd"
				xmlns:dc="http://purl.org/dc/elements/1.1/"
				xmlns:dcterms="http://purl.org/dc/terms/">
				<xsl:apply-templates/>
			</oai_dc:dc>
		</xsl:if>
	</xsl:template>
	<xsl:template match="record">
		<xsl:variable name="leader" select="leader"/>
		<xsl:variable name="leader6" select="substring($leader,7,1)"/>
		<xsl:variable name="leader7" select="substring($leader,8,1)"/>
		<xsl:variable name="controlField008" select="controlfield[@tag=008]"/>
        
        <dc:id>
            <xsl:value-of select="./controlfield[@tag='001']/text()" />
        </dc:id>
		
		<xsl:for-each select="datafield[@tag=300]">
			<dcterms:extent>
				
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">ab</xsl:with-param>
					<xsl:with-param name="delimeter"> : </xsl:with-param>
				</xsl:call-template>
				
				
			</dcterms:extent>
		</xsl:for-each>
		
        
		<xsl:for-each select="datafield[@tag=245]">
			<dc:title>
				
				<xsl:value-of select="subfield[@code='a']"/>

				<xsl:if test="subfield[@code='h']">
					<xsl:text> </xsl:text> <xsl:value-of select="subfield[@code='h']"/>	
				</xsl:if>
				<xsl:if test="subfield[@code='b']">
					<xsl:text> : </xsl:text> <xsl:value-of select="subfield[@code='b']"/>	
				</xsl:if>


			</dc:title>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=100]|datafield[@tag=700]">
			<dc:creator>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">aDbcd</xsl:with-param>
					<xsl:with-param name="delimeter">, </xsl:with-param>
				</xsl:call-template>
			</dc:creator>
		</xsl:for-each>
		<dc:type>
			<xsl:if test="$leader7='c'">
				<!--Remove attribute 6/04 jer-->
				<!--<xsl:attribute name="collection">yes</xsl:attribute>-->
				<xsl:text>collection</xsl:text>
			</xsl:if>
			<xsl:if test="$leader6='d' or $leader6='f' or $leader6='p' or $leader6='t'">
				<!--Remove attribute 6/04 jer-->
				<!--<xsl:attribute name="manuscript">yes</xsl:attribute>-->
				<xsl:text>manuscript</xsl:text>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$leader6='a' or $leader6='t'">text</xsl:when>
				<xsl:when test="$leader6='e' or $leader6='f'">cartographic</xsl:when>
				<xsl:when test="$leader6='c' or $leader6='d'">notated music</xsl:when>
				<xsl:when test="$leader6='i' or $leader6='j'">sound recording</xsl:when>
				<xsl:when test="$leader6='k'">still image</xsl:when>
				<xsl:when test="$leader6='g'">moving image</xsl:when>
				<xsl:when test="$leader6='r'">three dimensional object</xsl:when>
				<xsl:when test="$leader6='m'">software, multimedia</xsl:when>
				<xsl:when test="$leader6='p'">mixed material</xsl:when>
			</xsl:choose>
		</dc:type>
		<xsl:for-each select="datafield[@tag=655]">
			<dc:type>
				<xsl:value-of select="."/>
			</dc:type>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=260]">
			<dc:publisher>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">ab</xsl:with-param>
					<xsl:with-param name="delimeter"> : </xsl:with-param>
				</xsl:call-template>
			</dc:publisher>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=260]/subfield[@code='c']">
			<dc:date>
				<xsl:value-of select="."/>
			</dc:date>
		</xsl:for-each>
		<dc:language>
			<xsl:value-of select="substring($controlField008,36,3)"/>
		</dc:language>
		<xsl:for-each select="datafield[@tag=856]/subfield[@code='q']">
			<dc:format>
				<xsl:value-of select="."/>
			</dc:format>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=520]">
			<dcterms:abstract>
				<xsl:value-of select="subfield[@code='a']"/>
			</dcterms:abstract>
		</xsl:for-each>

		<xsl:for-each select="datafield[@tag=505]">
			<xsl:if test="subfield[@code='a']">
				<dcterms:abstract>
					<xsl:value-of select="subfield[@code='a']"/>
				</dcterms:abstract>
			</xsl:if>
			
			<xsl:if test="subfield[@code='g'] or subfield[@code='t'] or subfield[@code='r']">
				<dcterms:abstract>
					<xsl:value-of select="subfield[@code='g']"/><xsl:text> : </xsl:text><xsl:value-of select="subfield[@code='t']"/><xsl:text> / </xsl:text><xsl:value-of select="subfield[@code='r']"/>
				</dcterms:abstract>
			</xsl:if>

		</xsl:for-each>
		



		<xsl:for-each select="datafield[@tag=490]">
			 
			<dcterms:isPartof>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">av</xsl:with-param>
					<xsl:with-param name="delimeter"> .- </xsl:with-param>
				</xsl:call-template>
			</dcterms:isPartof>	
			
			
		</xsl:for-each>
		

		<xsl:for-each select="datafield[500&lt;= @tag and @tag&lt; 599 ][not(@tag=520 or @tag=505)]">
			<dc:description>
				<xsl:value-of select="subfield[@code='a']"/>
			</dc:description>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=600]">
			<dc:subject>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">abcdq</xsl:with-param>
				</xsl:call-template>
			</dc:subject>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=610]">
			<dc:subject>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">abcdq</xsl:with-param>
				</xsl:call-template>
			</dc:subject>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=611]">
			<dc:subject>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">abcdq</xsl:with-param>
				</xsl:call-template>
			</dc:subject>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=630]">
			<dc:subject>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">abcdq</xsl:with-param>
				</xsl:call-template>
			</dc:subject>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=650]">
			<dc:subject>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">abcdq</xsl:with-param>
				</xsl:call-template>
			</dc:subject>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=653]">
			<dc:subject>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">abcdq</xsl:with-param>
				</xsl:call-template>
			</dc:subject>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=752]">
			<dc:coverage>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">abcd</xsl:with-param>
				</xsl:call-template>
			</dc:coverage>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=530]">
			<dc:relation>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">abcdu</xsl:with-param>
				</xsl:call-template>
			</dc:relation>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=760]|datafield[@tag=762]|datafield[@tag=765]|datafield[@tag=767]|datafield[@tag=770]|datafield[@tag=772]|datafield[@tag=774]|datafield[@tag=776]|datafield[@tag=777]|datafield[@tag=786]|datafield[@tag=787]">
			<dc:relation>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">ot</xsl:with-param>
				</xsl:call-template>
			</dc:relation>
		</xsl:for-each>

		<xsl:for-each select="datafield[@tag=773]">
			<dc:source>
				<xsl:call-template name="subfieldSelect">
					<xsl:with-param name="codes">tg</xsl:with-param>
					<xsl:with-param name="delimeter"> .- </xsl:with-param>					
				</xsl:call-template>
			</dc:source>
		</xsl:for-each>
		

		<xsl:for-each select="datafield[@tag=775]">
			<dcterms:isVersionOf>
				<xsl:value-of select="subfield[@code='a']"/>
				
				<xsl:if test="subfield[@code='h']">
					<xsl:text> </xsl:text> <xsl:value-of select="subfield[@code='h']"/>	
				</xsl:if>
				
				<xsl:if test="subfield[@code='x']">
					<xsl:text> .- </xsl:text> <xsl:value-of select="subfield[@code='x']"/>	
				</xsl:if>

				<xsl:if test="subfield[@code='z']">
					<xsl:text> .- </xsl:text> <xsl:value-of select="subfield[@code='z']"/>	
				</xsl:if>
				
			</dcterms:isVersionOf>
		</xsl:for-each>
		


		<xsl:for-each select="datafield[@tag=780]">
			<dcterms:replaces>
				<xsl:value-of select="subfield[@code='a']"/>
				
				<xsl:if test="subfield[@code='h']">
					<xsl:text> </xsl:text> <xsl:value-of select="subfield[@code='h']"/>	
				</xsl:if>
				
				<xsl:if test="subfield[@code='x']">
					<xsl:text> .- </xsl:text> <xsl:value-of select="subfield[@code='x']"/>	
				</xsl:if>
			</dcterms:replaces>
		</xsl:for-each>
		

		<xsl:for-each select="datafield[@tag=785]">
			<dcterms:isReplacedBy>
				<xsl:value-of select="subfield[@code='a']"/>
				
				<xsl:if test="subfield[@code='h']">
					<xsl:text> </xsl:text> <xsl:value-of select="subfield[@code='h']"/>	
				</xsl:if>
				
				<xsl:if test="subfield[@code='x']">
					<xsl:text> .- </xsl:text> <xsl:value-of select="subfield[@code='x']"/>	
				</xsl:if>
			</dcterms:isReplacedBy>
		</xsl:for-each>
		


		<xsl:for-each select="datafield[@tag=856]">
			<dc:identifier>
				<xsl:value-of select="subfield[@code='u']"/>
			</dc:identifier>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=020]">
			<dc:identifier>
				<xsl:text>URN:ISBN:</xsl:text>
				<xsl:value-of select="subfield[@code='a']"/>
			</dc:identifier>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=506]">
			<dc:rights>
				<xsl:value-of select="subfield[@code='a']"/>
			</dc:rights>
		</xsl:for-each>
		<xsl:for-each select="datafield[@tag=540]">
			<dc:rights>
				<xsl:value-of select="subfield[@code='a']"/>
			</dc:rights>
		</xsl:for-each>



		<!--</oai_dc:dc>-->
	</xsl:template>
</xsl:stylesheet>

<!-- Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="..\..\..\..\..\..\..\..\..\..\javadev4\testsets\diacriticu8.xml" htmlbaseurl="" outputurl="" processortype="internal" useresolver="yes" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
-->