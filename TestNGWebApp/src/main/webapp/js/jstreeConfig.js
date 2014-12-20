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
                var node = ref.get_node(sel);
                ref.edit(sel);
                if(node.type == "suite_node") {
                	var uuid = guid();
                	ref.set_id(sel, uuid);
           			node.a_attr.id = uuid + "_anchor";
                }
            }
        },

        addSuite: {
            label: "Add Suite",
            action: createSuite,
            icon: "img/from_jdt/new_testsuite (1).gif"
        },
        addTestContainer: {
            label: "Add Test Container",
            action: createTestContainer,
            icon: "img/testcontainer_new.png"
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
                var ref = $('#jstree_scenario_builder').jstree(true);
                sel = ref.get_selected();
                currentParId = ref.get_parent(sel[0]);
                
                if (!sel.length) {
                    return false;
                }
               
                newContainer = ref.create_node(currentParId, {
                    "text": "new_test_container",
                    "type": "test_node"
                });
                if (newContainer) {
                    var newId = guid();
                	var container_node = ref.get_node(newContainer);
                	container_node.a_attr.id = newId + "_anchor";
                	ref.set_id(sel,newId);                  
                    ref.move_node(sel, newId);
                    ref.edit(newId);
                } else {
                	console.log("new gruop container not created");
                }

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



 function recursiveIteration(object, ref) {
    for (var property in object) {
        if (object.hasOwnProperty(property)) {
            if (typeof object[property] == "object"){
                recursiveIteration(object[property], ref);
            }else{
                if(property == "type") {
                	if(object["type"] == "test_method_node") {
                		ref.set_icon(object["id"],"img/test.gif");                		
                	} else if(object["type"] == "test_node") {
                		ref.set_icon(object["id"],"img/testcontainer.png");
                	} else if (object["type"] == "suite_node") {
                		ref.set_icon(object["id"],"img/suite.gif");
                	} else {
                		//skip
                	}
                	
                }
            }
        }
    }
}

function undeterminedToChecked(object, undeterminedArray) {
	for (var property in object) {
        if (object.hasOwnProperty(property)) {
            if (typeof object[property] == "object"){
            	undeterminedToChecked(object[property], undeterminedArray);
            } else {
            	if(property == "id") {
            		 if($.inArray(object[property],undeterminedArray) !== -1) {            			 
            			 console.log("changing the untetermine to checked for " + object[property]);
            			 object.state.checked = true;
            			 console.log(object);
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

function scrollReporterEnd(){
    $("#reporter").animate({ scrollTop: $("#reporter").height() }, "fast");
}


var ws1 = undefined; 
function execute() {
	
	if (typeof ws1 === 'undefined') {
		ws1 = new WebSocket("ws://localhost:8080/WebSocketTestServlet");
		ws1.onopen = function(event) {
			 console.log("clear last run");
			ref = $("#jstree_scenario_builder").jstree(true);
			recursiveIteration(ref.get_json('#', { 'flat' : false })[0], ref);
			 
			 console.log("connected: send execution...");
			 var jsonSuite = ref.get_json('#', { 'flat' : false })[0].children[0];
			 
			 var undeterminedArray = []
			 $.each($(".jstree-undetermined"), function(index, value) {
				 undeterminedArray.push($(value).parent().parent().attr("id"));  
			 });
			 var finalJson = undeterminedToChecked(jsonSuite, undeterminedArray);
			 
			 var jsonStringSuite = JSON.stringify(jsonSuite);
		     ws1.send(jsonStringSuite); 
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
				
				$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/testcontainer_run.png");
				
				finalReport += message.type + " - " + message.message;
				break;
			case "endContainer":
				if(hasFailures($("#jstree_scenario_builder").jstree(true).get_node(message.message))){
					$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/testcontainer_fail.png");
				} else {
					$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/testcontainer_ok.png");
				}
				finalReport += message.type + " - " + message.message;
				break;
			case "startSuite":
				
				$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/suiterun.gif");
				
				finalReport += message.type + " - " + message.message;
				break;
			case "endSuite":
				if(hasFailures($("#jstree_scenario_builder").jstree(true).get_node(message.message))){
					$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/suitefail.gif");
				} else {
					$("#jstree_scenario_builder").jstree(true).set_icon(message.message,"img/suiteok.gif");
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
			scrollReporterEnd()
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
	$(".jstree_scenario_builder").on('check_node.jstree', function(e, data) {
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
        fr.readAsText(file);
    });

    $("#jstree_scenario_builder").on('copy_node.jstree', function (e, data) {
		
        $.each(data.node.children_d, function (index, childID) {
            var ch_uuid = guid();
            var child_node = $('#jstree_scenario_builder').jstree(true).get_node(childID);
            child_node.a_attr.id = ch_uuid + "_anchor";
            $('#jstree_scenario_builder').jstree(true).set_id(childID, ch_uuid);
        });
		
        var uuid = guid();
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

//testInventoryDataDemo = {"text":"com.testng.tests","type":"root","children":[{"text":"com","type":"package_node","children":[{"text":"testng","type":"package_node","children":[{"text":"tests","type":"package_node","children":[{"text":"mock","type":"package_node","children":[{"text":"inner","type":"package_node","children":[{"text":"copy","type":"package_node","children":[{"text":"InnerMockTests","type":"class_node","children":[{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTe jsakkjjj kkkjjs jkkkasjd  fjjakjstNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"test2 - InnerMoclTestTest1","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"test2","testName":"InnerMoclTestTest1","testJavadoc":"javadoc for InnerMoclTestTest1"}},{"type":"test_method_node","text":"test3 - InnerMoclTestTest1","li_attr":{"className":"com.testng.tests.mock.inner.copy.InnerMockTests","methodName":"test3","testName":"InnerMoclTestTest1","testJavadoc":null}}]}]},{"text":"InnerMockTests","type":"class_node","children":[{"type":"test_method_node","text":"testNewName - InnerMoclTestTestNewName","li_attr":{"className":"com.testng.tests.mock.inner.InnerMockTests","methodName":"testNewName","testName":"InnerMoclTestTestNewName","testJavadoc":null}},{"type":"test_method_node","text":"test2 - InnerMoclTestTest1","li_attr":{"className":"com.testng.tests.mock.inner.InnerMockTests","methodName":"test2","testName":"InnerMoclTestTest1","testJavadoc":null}},{"type":"test_method_node","text":"test3 - InnerMoclTestTest1","li_attr":{"className":"com.testng.tests.mock.inner.InnerMockTests","methodName":"test3","testName":"InnerMoclTestTest1","testJavadoc":null}}]}]},{"text":"MockTests4","type":"class_node","children":[{"type":"test_method_node","text":"test1","li_attr":{"className":"com.testng.tests.mock.MockTests4","methodName":"test1","testName":"","testJavadoc":"javadoc for test1 in MockTests4","params":["first","second"]}}]},{"text":"MockTests5","type":"class_node","children":[{"type":"test_method_node","text":"test1","li_attr":{"className":"com.testng.tests.mock.MockTests5","methodName":"test1","testName":"","testJavadoc":null,"params":["first","second"]}}]}]},{"text":"MyCodeTests","type":"class_node","children":[{"type":"test_method_node","text":"testAdd - test add","li_attr":{"className":"com.testng.tests.MyCodeTests","methodName":"testAdd","testName":"test add","testJavadoc":null,"params":["num1","num2"]}}]},{"text":"Test1","type":"class_node","children":[{"type":"test_method_node","text":"testBroken","li_attr":{"className":"com.testng.tests.Test1","methodName":"testBroken","testName":"","testJavadoc":null,"params":["str","last"]}},{"type":"test_method_node","text":"testMethod3","li_attr":{"className":"com.testng.tests.Test1","methodName":"testMethod3","testName":"","testJavadoc":null}},{"type":"test_method_node","text":"testMethod5","li_attr":{"className":"com.testng.tests.Test1","methodName":"testMethod5","testName":"","testJavadoc":null}},{"type":"test_method_node","text":"testMethod1","li_attr":{"className":"com.testng.tests.Test1","methodName":"testMethod1","testName":"","testJavadoc":null}},{"type":"test_method_node","text":"testMethod2","li_attr":{"className":"com.testng.tests.Test1","methodName":"testMethod2","testName":"","testJavadoc":null}},{"type":"test_method_node","text":"throwExpectedException1ShouldPass","li_attr":{"className":"com.testng.tests.Test1","methodName":"throwExpectedException1ShouldPass","testName":"","testJavadoc":null}},{"type":"test_method_node","text":"throwExpectedException2ShouldPass","li_attr":{"className":"com.testng.tests.Test1","methodName":"throwExpectedException2ShouldPass","testName":"","testJavadoc":null}}]}]}]}]}]}

function saveParamsForTest() {
	id = $(".btn-save-params").attr("id");
	inputArr = $("#params-form").find("input.param");
	var paramsWithValues = [];
	$.each(inputArr, function(index, value){
		paramsWithValues.push(value.name + ":" + value.value);
	});
	
	inputTimeout = $("#params-form").find("input.timeout")
	$("#jstree_scenario_builder").jstree(true).get_node(id).li_attr.params = paramsWithValues;
	$("#jstree_scenario_builder").jstree(true).get_node(id).li_attr.timeout = inputTimeout.val();
	
}

function createTestContainer() {
    var ref = $('#jstree_scenario_builder').jstree(true);
    sel = ref.get_selected();
    if (!sel.length) {
        return false;
    }
    sel = sel[0];
    sel = ref.create_node(sel, {
        "text": "new_test_container",
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

//function handleAddSuite(data) {
//	var jsonData = data.original.data.jsonSuite;
//	console.log(jsonData);
//	newParentType = $("#jstree_scenario_builder").jstree(true).get_node(data.parent).type;
//	if(newParentType == "root") {
//		$('#jstree_scenario_builder').jstree(true).settings.core.data = jsonData;
//        $('#jstree_scenario_builder').jstree(true).refresh();
//	} else {
//		var newPatent = $("#jstree_scenario_builder").jstree(true).get_node(data.parent);
//		newParent.
//		treeJson = $("#jstree_scenario_builder").jstree(true).get_json('#', { 'flat' : false })[0]);
//		jsonData.children[0].type = "test_node";
//		
//	}
//}

function createSuite() {
    var ref = $('#jstree_scenario_builder').jstree(true);
    sel = ref.get_selected();
    if (!sel.length) {
        return false;
    }
    sel = sel[0];
    sel = ref.create_node(sel, {
        "text": "new_suite",
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