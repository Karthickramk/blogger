var tags = [];
availableTags = [];
function onlyUnique(value, index, self) { 
    return self.indexOf(value) === index;
}
$(document).ready(function() {
	function split( val ) {
		return val.split( /,\s*/ );
	}
	function extractLast( term ) {
		return split( term ).pop();
	}
	$.ajax({
		url : '/unprotected/tags',
		type : 'get',
		accept : 'application/json',
		async: false,
		success : function(data) {
			tags = data.tags;
			$.each(tags, function(index) {
				availableTags.push(tags[index]);
			});
		}
	});
	$( "#search_input" ).autocomplete({
		source:''
	});
	$( "#search_input" )
	// don't navigate away from the field on tab when selecting an item
	.on( "keydown", function( event ) {
		if ( event.keyCode === $.ui.keyCode.TAB &&
				$( this ).autocomplete( "instance" ).menu.active ) {
			event.preventDefault();
		}
	})
	.autocomplete({
		minLength: 0,
		source: function( request, response ) {
			// delegate back to autocomplete, but extract the last term
			availableTags = availableTags.filter( onlyUnique );
			response( $.ui.autocomplete.filter(
				availableTags, extractLast( request.term ) ) );
		},
		focus: function() {
			// prevent value inserted on focus
			return false;
		},
		select: function( event, ui ) {
			var terms = split( this.value );
			// remove the current input
			terms.pop();
			// add the selected item
			terms.push( ui.item.value );
			// add placeholder to get the comma-and-space at the end
			terms.push( "" );
			this.value = terms.join( "," );
			return false;
		}
	});
	var options = '';
    for (var x = 0; x < availableTags.length; x++) {
        options += '<option value="' + availableTags[x] + '">' + availableTags[x] + '</option>';
    }
    $('#interest').html(options);
});
function getTags(data){
	$.each(data, function(index) {
		tags = tags.concat(data[index].tags);
		availableTags = tags.filter( onlyUnique );
    });
}