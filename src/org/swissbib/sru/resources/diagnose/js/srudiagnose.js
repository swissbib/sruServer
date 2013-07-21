

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

    }

    //alert (query);



}



$(function () {

    $("ul.nav li.querysubmit a").click(function () {
        collectFormValues();
    });

    $("#submitCQL").click(function () {
        collectFormValues();
    });



});
