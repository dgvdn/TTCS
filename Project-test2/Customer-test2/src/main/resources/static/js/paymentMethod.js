$(document).ready(function(){ 
    $("#debit").click(function() {
        $(".byBank").hide();
    }); 
});
$(document).ready(function(){ 
    $("#credit").click(function() {
        $(".byBank").show();
    }); 
});