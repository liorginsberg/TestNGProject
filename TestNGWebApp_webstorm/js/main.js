
var guid = (function() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }
    return function() {
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
            s4() + '-' + s4() + s4() + s4();
    };
})();



function customMenu(node) {
    // The default set of all items
    var items = {
        renameItem: { // The "rename" menu item
            label: "Rename",
            action: function () {
            }
        },
        deleteItem: { // The "delete" menu item
            label: "Delete",
            action: function () {
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

    if ($(node).hasClass("folder")) {
        // Delete the "delete" menu item
        delete items.deleteItem;
    }

    return items;
}
$(function() {
    $("#jstree_scenario_builder").on('copy_node.jstree', function (e, data) {
        console.log(e);
        console.log(data);
        var uuid = guid();
        console.log("the new instace by copy is getting uuid:" + uuid);
        $.each(data.node.children_d, function(index, childID){
            childID = "#" + childID;
            console.log("child " + index + ":");
            console.log($(childID))
        });
        console.log(data.node.a_attr.id = uuid);
    });
});

function loadProject() {
    $.getJSON( "data/testJsonMock.json", function( data ) {
        console.log(data);
        buildTree(data);


    });
}

function buildTree(data) {
    var node = {
        id: guid(),
        text:data.name
    }
    if(data.type == "file") {
        node.type = "class_node";
        if(node.has){}
    }
}

/*
 public static JsonObject getDirectoryAsJson(File mainFile) throws Exception {
    JsonObject node = new JsonObject();
    node.addProperty("name", removeFileExtention(mainFile));
     if (mainFile.isFile()) {
        node.addProperty("type", "file");
        if (isClassFile(mainFile)) {
            node.addProperty("file_type", "class");
            JsonArray testMethods = getTestMethods(mainFile);
            if (testMethods == null) {
                node.addProperty("file_type", "non_class");
            } else {
                node.add("test_methods", testMethods);
            }
        } else {
             node.addProperty("file_type", "non_class");
        }
    } else {
        node.addProperty("type", "directory");
        JsonArray children = new JsonArray();
        for (File file : mainFile.listFiles()) {
            JsonObject obj = getDirectoryAsJson(file);
            if (obj.has("file_type") && !("class".equals(obj.get("file_type").getAsString()))) {
                continue;
            }
            children.add(obj);
        }
        node.add("children", children);
    }
    return node;
 }
 */


testInventoryData = {
    "id": "inv-root",
    "text": "com.testng.tests",
    "type": "root",
    "children": [

        {
            "id": "package2",
            "text": "com",
            "type": "package_node",
            "children": [
                {
                    "id": "package3",
                    "text": "testng",
                    "type": "package_node",
                    "children": [
                        {
                            "id": "package4",
                            "text": "tests",
                            "type": "package_node",
                            "children": [
                                {
                                    "id": "class1",
                                    "text": "MyCodeTests",
                                    "type": "class_node",
                                    "children": [
                                        {
                                            "id": "tset1",
                                            "text": "test1 = desc here",
                                            "type": "test_method_node",
                                            "li_attr" : {"person" : "[lior,lior2]","class" : "method", "href" : "com.testng.tests.MyCodeTests.test1"}
                                        },
                                        {
                                            "id": "tset2",
                                            "text": "test2 = desc here",
                                            "type": "test_method_node",
                                            "li_attr" : {"class" : "method", "href" : "com.testng.tests.MyCodeTests.test2"}
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
}

;
testData = {
    "id": "root_suite",
    "text": "Root Suite",
    "type": "root_suite_type",
    "children": [
        {
            "id": "master_suite",
            "text": "Sanity",
            "type": "suite",
            "li_attr" : {"class":"master_suite_class"},
            "children": [
                {
                    "id": "t0",
                    "text": "test0",
                    "type": "test"
                },
                {
                    "id": "t1",
                    "text": "test1",
                    "type": "test"
                },
                {
                    "id": "t2",
                    "text": "test2",
                    "type": "test"
                }
            ]
        }
    ]
};