
/**
 * utility functions to control the flow of the swissbib SRI diagnose form
 *
 *  initial work July / August 2013
 *
 * Copyright (C) project swissbib, University Library Basel, Switzerland
 * http://www.swissbib.org  / http://www.swissbib.ch / http://www.ub.unibas.ch
 * Date: 8/1/13
 * Time: 11:40 AM
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * license:  http://opensource.org/licenses/gpl-2.0.php GNU General Public License
 *
 * @author Guenter Hipler <guenter.hipler@unibas.ch>
 * @version 0.5 development version
 * @requires jquery,
 */


/**
 * all values of the
 */
function collectFormValues() {

    var counter = 1;
    var selectedValues = [];
    var rowValues = {};

    $("div.controlvalues div.controls > :first-child").each(function () {
        if (counter % 4 == 0)  {
            rowValues[this.name] = this.value;
            if (rowValues['inputvalue'] != "") {
                selectedValues.push(rowValues);
            }
            rowValues = {};
            counter = 1;
        } else {
            rowValues[this.name] = this.value;
            counter++;
        }

    });


    var query = "";

    for (var rows = 0; rows < selectedValues.length; rows++ ) {
        if (rows > 0) {
            query +=  " " + selectedValues[rows -1]["booloptions"];
        }
        query +=  " " + selectedValues[rows]["fieldname"] + " " + selectedValues[rows]["relators"] + " " + selectedValues[rows]["inputvalue"];
    }

    if (query != "") {

        $("form#frmSRUsend input[name=query]").val(query);

        $("form#frmSRUsend").submit();

    } else {
        alert ("you have to fill in at least the value for one search field")
    }
    //alert (query);

}



$(function () {

/*
    $('#frmSRUcql').keydown(function(e) {
        if (e.keyCode == 13) {
            $('.querysubmit').click();
        }
    });


    $('#frmSRUsend').keydown(function(e) {
        if (e.keyCode == 13) {
            $('.querysubmit').click();;
        }
    });

*/



    var searchFieldNamesOptions = {

            //is created dynamically using velocity templates
            #foreach( $sruIndex in $sruIndexes )
               "$sruIndex" :  "$sruIndex",
            #end

    };


    $.each($(".searchFieldNames"), function() {

        //Closure is necessary
        var currentSelect = this;
        $.each(searchFieldNamesOptions, function(key, value) {
            $(currentSelect).append($('<option>', { value : key })
                    .text(value));
        });
    });




    $("ul.nav li.querysubmit a").click(function () {
        collectFormValues();
    });

    $("#submitCQL").click(function () {
        collectFormValues();
    });


    $("ul.nav li.rowadd a").click(function () {

        var allRows = $("div.row-fluid.controlvalues");

        if (allRows.length >= 10 ) {
            alert ("no more elements");
        }else {
            $("#rowCopyElement").clone().css("display","block").addClass("row-fluid").addClass("controlvalues").appendTo(allRows.last());
        }


    });

    $("ul.nav li.rowremove a").click(function () {

        var allRows = $("div.row-fluid.controlvalues");

        if (allRows.length <= 1 ) {
            alert("at least one element!");
        }else {
            allRows.last().remove();
        }

    });

});
