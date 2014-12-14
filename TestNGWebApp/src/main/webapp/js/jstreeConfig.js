var currentProject = "";

var guid = (function () {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    return function () {
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
            s4() + '-' + s4() + s4() + s4();
    };
})();


var currentNode;
function customMenu(node) {
    // The default set of all items
    currentNode = node;
    var items = {

        renameItem: { // The "rename" menu item
            label: "Rename",
            action: function () {
                var ref = $('#jstree_scenario_builder').jstree(true);
                sel = ref.get_selected();
                ref.edit(sel);
            }
        },

        addSuite: {
            label: "Add Suite",
            action: createSuite
        },
        addTestContainer: {
            label: "Add Test Container",
            action: createTestContainer
        },
        deleteItem: { // The "delete" menu item
            label: "Delete",
            action: function () {
                var ref = $('#jstree_scenario_builder').jstree(true);
                sel = ref.get_selected();
                ref.delete_node(sel);
            }
        },
        group: { // The "group" menu item
            label: "Group",
            action: function () {
                var ref = $('#jstree_demo').jstree(true);
                sel = ref.get_selected();
                currentParId = ref.get_parent(sel[0]);

                newSuiteId = ref.create_node(currentParId, {
                    "text": "New Suite",
                    "type": "suite"
                });
                ref.move_node(sel, newSuiteId);
                ref.edit(newSuiteId);

            }
        }
    };
    console.log(node.type);
    if (node.type != "suite_node" && node.type != "test_node") {
        console.log("The item will not have addTestContainer");
        delete items.addTestContainer;
    }
    if (node.type != "root") {
        console.log("The item will not have addSuite");
        delete items.addSuite;
    }

    return items;
}



 function recursiveIteration(object) {
    for (var property in object) {
        if (object.hasOwnProperty(property)) {
            if (typeof object[property] == "object"){
                recursiveIteration(object[property]);
            }else{
                if(property == "type") {
                	if(object["type"] == "test_method_node" || object["type"] == "test_node") {
                		$("#jstree_scenario_builder").jstree(true).set_icon(object["id"],"img/test.gif");
                	}
                	
                }
            }
        }
    }
}

function hasFailures(node) {
	var hasFailure = false;
	$.each(node.children_d,function(index, value) {
     	childNode = $("#jstree_scenario_builder").jstree(true).get_node(value);
		if(childNode.icon == "img/testfail.gif") {		
			hasFailure = true;
			return false;
		}
    });
    return hasFailure;
}

var ws1 = undefined; 
function execute() {
	
	if (typeof ws1 === 'undefined') {
		ws1 = new WebSocket("ws://localhost:8080/WebSocketTestServlet");
		ws1.onopen = function(event) {
			 console.log("clear last run");
			 
			recursiveIteration($("#jstree_scenario_builder").jstree(true).get_json('#', { 'flat' : false })[0]);
			 
			 console.log("connected: send execution...");
		     ws1.send(JSON.stringify($("#jstree_scenario_builder").jstree(true).get_json('#', { 'flat' : false })[0])); 
		}
		ws1.onmessage = function(event) {
			var message = $.parseJSON(event.data);
			var finalReport = message.date + ": ";
			switch (message.type) {
			case "startExecution":
				finalReport += "Execution Start";
				break;
			case "finishExecution":
				ws1.close();
				ws1 = undefined; 
				finalReport += "Execution Finish";
				break;
			case "startContainer":
				
				$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/testrun.gif");
				
				finalReport += message.type + " - " + message.message;
				break;
			case "endContainer":
				if(hasFailures($("#jstree_scenario_builder").jstree(true).get_node(message.message))){
					$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/testfail.gif");
				} else {
					$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/testok.gif");
				}
				finalReport += message.type + " - " + message.message;
				break;
			case "start":
				$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/testrun.gif");
				$("#" + message.message + "_anchor").addClass("test_bold");
				finalReport += message.type + " - " + message.message;
				break;
			case "testStart":
				$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/testrun.gif");
				finalReport += message.type + " - " + message.message;
				break;
			case "finish":
				finalReport += message.type + " - " + message.message;
				break;
			case "testSuccess":
				$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/testok.gif");
				finalReport += message.type + " - " + message.message;
				$("#" + message.message + "_anchor").removeClass("test_bold");
				break;
			case "testFail":
				$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/testfail.gif");
				finalReport += message.type + " - " + message.message;
				$("#" + message.message + "_anchor").removeClass("test_bold");

				break;
			case "testSkip":
				$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/testskip.gif");
				finalReport += message.type + " - " + message.message;
				$("#" + message.message + "_anchor").removeClass("test_bold");

				break;
			default:
				finalReport += message.type + " - " + message.message;
				break;
			}
			console.log(finalReport);
			$("#reporter").append("<p>" + finalReport + "</p>");
		}
		ws1.onerror = function(event) {
			console.log("ws1.onerror");
			console.log(event);
		}
		ws1.onclose = function(event) {
			console.log("ws1.onclose");
			console.log(event);
			ws1 = undefined; 
		}
	}
}


function expandAllTree(jstreeId) {
	var jstreeId = "#" + jstreeId; 
	$(jstreeId).jstree(true).open_all();
}
function collapseAllTree(jstreeId) {
	var jstreeId = "#" + jstreeId; 
	$(jstreeId).jstree(true).close_all();
}
function clearReport() {
	$("#reporter").empty();
}


var handleParents = [];
var handleChildren = [];
var handleChildren = false;


$(function() {
	

	//$("html").niceScroll();
	//$("#jstree_test_inventory").niceScroll();
	//$("#jstree_scenario_builder").niceScroll();
	$("#jstree_scenario_builder").on('check_node.jstree', function(e, data) {
	    /*var nodeId = data.node.id;
	    var parentNodeId = data.node.parent;
	   
	   	if(triggerCheck = "") {
	   		triggerCheck = nodeId;
	   	}
	   
	    $.each(data.node.parents, function (index, parentNodeId) {
	    	if($.inArray(parentNodeId, handleParents)) {
	    		console.log("skip parent check");
	    	} else {
	    		handleParents.push(parentNodeId);
	    		$("#jstree_scenario_builder").jstree(true).check_node(parentNodeId);
	    	}
	    });
	    if($.inArray(parentNodeId, handleParents)) {
	    	console.log("skip children check");
	    } else {
		    $.each(data.node.children_d, function (index, childID) {
		    	$("#jstree_scenario_builder").jstree(true).check_node(childID);
		    });
	    }
	     console.log("round finished")
	    handleParents = [];
	    return false;*/
	});
	
    fr = new FileReader();
    fr.onload = receivedText;
    if (window.File && window.FileReader && window.FileList && window.Blob) {
        console.log("Great The browser is supported")
    } else {
        alert('The File APIs are not fully supported in this browser.');
    }
    $("input").change(function () {

        file = this.files[0];
        selectedFile = file;
        console.log(file);
        fr.readAsText(file);
    });

    $("#jstree_scenario_builder").on('copy_node.jstree', function (e, data) {
        console.log(e);

        $.each(data.node.children_d, function (index, childID) {
            var ch_uuid = guid();
            console.log("child " + index + ":");
            var child_node = $('#jstree_scenario_builder').jstree(true).get_node(childID);
//            child_node.li_attr.id = ch_uuid;
            child_node.a_attr.id = ch_uuid + "_anchor";
            $('#jstree_scenario_builder').jstree(true).set_id(childID, ch_uuid);
        });

        var uuid = guid();
//        data.node.li_attr.id = uuid;
        data.node.a_attr.id = uuid + "_anchor";
        $('#jstree_scenario_builder').jstree(true).set_id(data.node.id, uuid);
    });
});

// not in use maybe later we will have problems
function setUUIDNested(node, new_parent_id) {

    node.parent = new_parent_id;
    node.li_attr.id = uuid;
    node.a_attr.id = uuid + "_anchor";
    if (node.hasOwnProperty("children")) {

        $.each(node.children, function(index, childID){
            child_node = $('#jstree_scenario_builder').jstree(true).get_node(childID);
            setUUIDNested(child_node, node.li_attr.id);
        });
    }

}


function receivedText() {
    console.log(fr.result);
    var lines = fr.result.split('\n');
    $.each(lines, function () {
        property = this.split("=");
        if (property[0] == "BASE_DIR") {
            console.log("project location on file system: " + property[1]);
            console.log("Now send this to the server for further processing");
            console.log($("input").val());
            var propFileLoc = property[1].replace(/(\r\n|\n|\r)/gm, "");
            propFileLoc = propFileLoc + "\\" + $('input').val().split(/(\\|\/)/g).pop()
            currentProject = propFileLoc;
            $.ajax({
                url: "ProjectLoaderServlet",
                type: 'POST',
                dataType: 'json',
                data: "projLoc=" + currentProject,

                success: function (data) {
                    $('#jstree_test_inventory').jstree(true).settings.core.data = data;
                    $('#jstree_test_inventory').jstree(true).refresh();
                },
                error: function (data, status, er) {
                    alert("error: " + data + " status: " + status + " er:" + er);
                }
            });
        }
    });
}

function refreshTestProject() {
    console.log("refreshing...")

    $.ajax({
        url: "ProjectLoaderServlet",
        type: 'POST',
        dataType: 'json',
        data: "projLoc=" + currentProject + "&preventCache=" + new Date(),

        success: function (data) {
            $('#jstree_test_inventory').jstree(true).settings.core.data = data;
            $('#jstree_test_inventory').jstree(true).refresh();
        },
        error: function (data, status, er) {
            alert("error: " + data + " status: " + status + " er:" + er);
        }
    });


}


testInventoryData = {
    "text": "default",
    "type": "root",
    "children": []
};

function saveParamsForTest() {
	id = $(".btn-save-params").attr("id");
	inputArr = $("#params-form").find("input");
	var paramsWithValues = [];
	$.each(inputArr, function(index, value){
		paramsWithValues.push(value.name + ":" + value.value);
	});
	$("#jstree_scenario_builder").jstree(true).get_node(id).li_attr.params = paramsWithValues;
	
}

function createTestContainer() {
    var ref = $('#jstree_scenario_builder').jstree(true);
    sel = ref.get_selected();
    if (!sel.length) {
        return false;
    }
    sel = sel[0];
    sel = ref.create_node(sel, {
        "text": "New Test Container",
        "type": "test_node"
    });
    if (sel) {
        var newId = guid();
    	var container_node = ref.get_node(sel);
    	container_node.a_attr.id = newId + "_anchor";
    	ref.set_id(sel,newId);
        ref.edit(newId);
    }
}

function createSuite() {
    var ref = $('#jstree_scenario_builder').jstree(true);
    sel = ref.get_selected();
    if (!sel.length) {
        return false;
    }
    sel = sel[0];
    sel = ref.create_node(sel, {
        "text": "New Suite",
        "type": "suite_node"
    });
    if (sel) {
    	var newId = guid();
    	var suite_node = ref.get_node(sel);
    	suite_node.a_attr.id = newId + "_anchor";
    	ref.set_id(sel,newId);
        ref.edit(newId);
    }
}