var currentProject = "";

var guid = (function() {
	function s4() {
		return Math.floor((1 + Math.random()) * 0x10000).toString(16)
				.substring(1);
	}
	return function() {
		return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4()
				+ s4() + s4();
	};
})();

function executeTests() {

}

function customMenu(node) {
	// The default set of all items
	var items = {
		renameItem : { // The "rename" menu item
			label : "Rename",
			action : function() {
			}
		},
		deleteItem : { // The "delete" menu item
			label : "Delete",
			action : function() {
			}
		},
		group : { // The "group" menu item
			label : "Group",
			action : function() {
				var ref = $('#jstree_demo').jstree(true);
				sel = ref.get_selected();
				currentParId = ref.get_parent(sel[0]);

				newSuiteId = ref.create_node(currentParId, {
					"text" : "New Suite",
					"type" : "suite"
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


	


	fr = new FileReader();
	fr.onload = receivedText;
	if (window.File && window.FileReader && window.FileList && window.Blob) {
		console.log("Great The browser is supported")
	} else {
		alert('The File APIs are not fully supported in this browser.');
	}
	$("input").change(function() {

		file = this.files[0];
		selectedFile = file;
		console.log(file);
		fr.readAsText(file);
	});

	$("#jstree_scenario_builder").on(
			'copy_node.jstree',
			function(e, data) {
				console.log(e);
				console.log(data);
				var uuid = guid();
				console.log("the new instace by copy is getting uuid:" + uuid);
				$.each(data.node.children_d, function(index, childID) {
					childID = "#" + childID;
					console.log("child " + index + ":");
					var child_node = $('#tree-selector').jstree(true).get_node(
							'node_id_here');
					console.log(child_node);
				});
				console.log(data.node.a_attr.id = uuid);
			});
});

function receivedText() {
	console.log(fr.result);
	var lines = fr.result.split('\n');
	$
			.each(
					lines,
					function() {
						property = this.split("=");
						if (property[0] == "BASE_DIR") {
							console.log("project location on file system: "
									+ property[1]);
							console
									.log("Now send this to the server for further processing");
							console.log($("input").val());
							var propFileLoc = property[1].replace(
									/(\r\n|\n|\r)/gm, "");
							propFileLoc = propFileLoc + "\\"
									+ $('input').val().split(/(\\|\/)/g).pop()
							currentProject = propFileLoc;
							$
									.ajax({
										url : "ProjectLoaderServlet",
										type : 'POST',
										dataType : 'json',
										data : "projLoc=" + currentProject,

										success : function(data) {
											$('#jstree_test_inventory').jstree(
													true).settings.core.data = data;
											$('#jstree_test_inventory').jstree(
													true).refresh();
										},
										error : function(data, status, er) {
											alert("error: " + data
													+ " status: " + status
													+ " er:" + er);
										}
									});
						}
					});
}

function refreshTestProject() {
	console.log("refreshing...")

	$.ajax({
		url : "ProjectLoaderServlet",
		type : 'POST',
		dataType : 'json',
		data : "projLoc=" + currentProject + "&preventCache=" + new Date(),

		success : function(data) {
			$('#jstree_test_inventory').jstree(true).settings.core.data = data;
			$('#jstree_test_inventory').jstree(true).refresh();
		},
		error : function(data, status, er) {
			alert("error: " + data + " status: " + status + " er:" + er);
		}
	});

}

/*
 * public static JsonObject getDirectoryAsJson(File mainFile) throws Exception {
 * JsonObject node = new JsonObject(); node.addProperty("name",
 * removeFileExtention(mainFile)); if (mainFile.isFile()) {
 * node.addProperty("type", "file"); if (isClassFile(mainFile)) {
 * node.addProperty("file_type", "class"); JsonArray testMethods =
 * getTestMethods(mainFile); if (testMethods == null) {
 * node.addProperty("file_type", "non_class"); } else { node.add("test_methods",
 * testMethods); } } else { node.addProperty("file_type", "non_class"); } } else {
 * node.addProperty("type", "directory"); JsonArray children = new JsonArray();
 * for (File file : mainFile.listFiles()) { JsonObject obj =
 * getDirectoryAsJson(file); if (obj.has("file_type") &&
 * !("class".equals(obj.get("file_type").getAsString()))) { continue; }
 * children.add(obj); } node.add("children", children); } return node; }
 */

testInventoryData = {
	"text" : "com.testng.tests",
	"type" : "root",
	"children" : [ {
		"text" : "com",
		"type" : "package_node",
		"children" : [ {
			"text" : "testng",
			"type" : "package_node",
			"children" : [ {
				"text" : "tests",
				"type" : "package_node",
				"children" : [
						{
							"text" : "mock",
							"type" : "package_node",
							"children" : [
									{
										"text" : "inner",
										"type" : "package_node",
										"children" : [
												{
													"text" : "copy",
													"type" : "package_node",
													"children" : [ {
														"text" : "InnerMockTests",
														"type" : "class_node",
														"children" : [
																{
																	"type" : "test_method_node",
																	"text" : "test3 - InnerMoclTestTest1",
																	"li_attr" : {
																		"className" : "com.testng.tests.mock.inner.copy.InnerMockTests",
																		"methodName" : "test3",
																		"testName" : "InnerMoclTestTest1"
																	}
																},
																{
																	"type" : "test_method_node",
																	"text" : "test2 - InnerMoclTestTest1",
																	"li_attr" : {
																		"className" : "com.testng.tests.mock.inner.copy.InnerMockTests",
																		"methodName" : "test2",
																		"testName" : "InnerMoclTestTest1"
																	}
																},
																{
																	"type" : "test_method_node",
																	"text" : "testNewName - InnerMoclTestTestNewName",
																	"li_attr" : {
																		"className" : "com.testng.tests.mock.inner.copy.InnerMockTests",
																		"methodName" : "testNewName",
																		"testName" : "InnerMoclTestTestNewName"
																	}
																} ]
													} ]
												},
												{
													"text" : "InnerMockTests",
													"type" : "class_node",
													"children" : [
															{
																"type" : "test_method_node",
																"text" : "test3 - InnerMoclTestTest1",
																"li_attr" : {
																	"className" : "com.testng.tests.mock.inner.InnerMockTests",
																	"methodName" : "test3",
																	"testName" : "InnerMoclTestTest1"
																}
															},
															{
																"type" : "test_method_node",
																"text" : "test2 - InnerMoclTestTest1",
																"li_attr" : {
																	"className" : "com.testng.tests.mock.inner.InnerMockTests",
																	"methodName" : "test2",
																	"testName" : "InnerMoclTestTest1"
																}
															},
															{
																"type" : "test_method_node",
																"text" : "testNewName - InnerMoclTestTestNewName",
																"li_attr" : {
																	"className" : "com.testng.tests.mock.inner.InnerMockTests",
																	"methodName" : "testNewName",
																	"testName" : "InnerMoclTestTestNewName"
																}
															} ]
												} ]
									},
									{
										"text" : "CopyOfCopyOfMockTests",
										"type" : "class_node",
										"children" : [
												{
													"type" : "test_method_node",
													"text" : "test12",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test12",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test13",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test13",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test14",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test14",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test11",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test11",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test10",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test10",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test9",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test9",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test8",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test8",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test7",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test7",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test6",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test6",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test4",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test4",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test5",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test5",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test3",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test3",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test2",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test2",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test1",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test1",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test51",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test51",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test16",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test16",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test17",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test17",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test18",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test18",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test19",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test19",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test20",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test20",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test21",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test21",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test23",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test23",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test24",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test24",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test25",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test25",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test126",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfCopyOfMockTests",
														"methodName" : "test126",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												} ]
									},
									{
										"text" : "CopyOfMockTests",
										"type" : "class_node",
										"children" : [
												{
													"type" : "test_method_node",
													"text" : "test3",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfMockTests",
														"methodName" : "test3",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test2",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfMockTests",
														"methodName" : "test2",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test1",
													"li_attr" : {
														"className" : "com.testng.tests.mock.CopyOfMockTests",
														"methodName" : "test1",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												} ]
									},
									{
										"text" : "MockTests",
										"type" : "class_node",
										"children" : [
												{
													"type" : "test_method_node",
													"text" : "test3",
													"li_attr" : {
														"className" : "com.testng.tests.mock.MockTests",
														"methodName" : "test3",
														"testName" : ""
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test2",
													"li_attr" : {
														"className" : "com.testng.tests.mock.MockTests",
														"methodName" : "test2",
														"testName" : ""
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test1",
													"li_attr" : {
														"className" : "com.testng.tests.mock.MockTests",
														"methodName" : "test1",
														"testName" : ""
													}
												} ]
									},
									{
										"text" : "MockTests2",
										"type" : "class_node",
										"children" : [
												{
													"type" : "test_method_node",
													"text" : "test3",
													"li_attr" : {
														"className" : "com.testng.tests.mock.MockTests2",
														"methodName" : "test3",
														"testName" : ""
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test2",
													"li_attr" : {
														"className" : "com.testng.tests.mock.MockTests2",
														"methodName" : "test2",
														"testName" : ""
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test1",
													"li_attr" : {
														"className" : "com.testng.tests.mock.MockTests2",
														"methodName" : "test1",
														"testName" : ""
													}
												} ]
									},
									{
										"text" : "MockTests3",
										"type" : "class_node",
										"children" : [ {
											"type" : "test_method_node",
											"text" : "test1",
											"li_attr" : {
												"className" : "com.testng.tests.mock.MockTests3",
												"methodName" : "test1",
												"testName" : "",
												"params" : [ "first", "second" ]
											}
										} ]
									},
									{
										"text" : "MockTests4",
										"type" : "class_node",
										"children" : [ {
											"type" : "test_method_node",
											"text" : "test1",
											"li_attr" : {
												"className" : "com.testng.tests.mock.MockTests4",
												"methodName" : "test1",
												"testName" : "",
												"params" : [ "first", "second" ]
											}
										} ]
									},
									{
										"text" : "MockTests5",
										"type" : "class_node",
										"children" : [ {
											"type" : "test_method_node",
											"text" : "test1",
											"li_attr" : {
												"className" : "com.testng.tests.mock.MockTests5",
												"methodName" : "test1",
												"testName" : "",
												"params" : [ "first", "second" ]
											}
										} ]
									} ]
						},
						{
							"text" : "mockitagain",
							"type" : "package_node",
							"children" : [
									{
										"text" : "CopyOfCopyOfMockTests",
										"type" : "class_node",
										"children" : [
												{
													"type" : "test_method_node",
													"text" : "test12",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test12",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test13",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test13",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test14",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test14",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test11",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test11",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test10",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test10",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test9",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test9",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test8",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test8",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test7",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test7",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test6",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test6",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test4",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test4",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test5",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test5",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test3",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test3",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test2",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test2",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test1",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test1",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test51",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test51",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test16",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test16",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test17",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test17",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test18",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test18",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test19",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test19",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test20",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test20",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test21",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test21",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test23",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test23",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test24",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test24",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test25",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test25",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test126",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfCopyOfMockTests",
														"methodName" : "test126",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												} ]
									},
									{
										"text" : "CopyOfMockTests",
										"type" : "class_node",
										"children" : [
												{
													"type" : "test_method_node",
													"text" : "test12",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test12",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test13",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test13",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test14",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test14",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test11",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test11",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test10",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test10",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test9",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test9",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test8",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test8",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test7",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test7",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test6",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test6",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test4",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test4",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test5",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test5",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test3",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test3",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test2",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test2",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test1",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test1",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test51",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test51",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test16",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test16",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test17",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test17",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test18",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test18",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test19",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test19",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test20",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test20",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test21",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test21",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test23",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test23",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test24",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test24",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test25",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test25",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test126",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.CopyOfMockTests",
														"methodName" : "test126",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												} ]
									},
									{
										"text" : "MockTests",
										"type" : "class_node",
										"children" : [
												{
													"type" : "test_method_node",
													"text" : "test3",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.MockTests",
														"methodName" : "test3",
														"testName" : ""
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test2",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.MockTests",
														"methodName" : "test2",
														"testName" : ""
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test1",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.MockTests",
														"methodName" : "test1",
														"testName" : ""
													}
												} ]
									},
									{
										"text" : "MockTests2",
										"type" : "class_node",
										"children" : [
												{
													"type" : "test_method_node",
													"text" : "test3",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.MockTests2",
														"methodName" : "test3",
														"testName" : ""
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test2",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.MockTests2",
														"methodName" : "test2",
														"testName" : ""
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test1",
													"li_attr" : {
														"className" : "com.testng.tests.mockitagain.MockTests2",
														"methodName" : "test1",
														"testName" : ""
													}
												} ]
									},
									{
										"text" : "MockTests3",
										"type" : "class_node",
										"children" : [ {
											"type" : "test_method_node",
											"text" : "test1",
											"li_attr" : {
												"className" : "com.testng.tests.mockitagain.MockTests3",
												"methodName" : "test1",
												"testName" : "",
												"params" : [ "first", "second" ]
											}
										} ]
									},
									{
										"text" : "MockTests4",
										"type" : "class_node",
										"children" : [ {
											"type" : "test_method_node",
											"text" : "test1",
											"li_attr" : {
												"className" : "com.testng.tests.mockitagain.MockTests4",
												"methodName" : "test1",
												"testName" : "",
												"params" : [ "first", "second" ]
											}
										} ]
									},
									{
										"text" : "MockTests5",
										"type" : "class_node",
										"children" : [ {
											"type" : "test_method_node",
											"text" : "test1",
											"li_attr" : {
												"className" : "com.testng.tests.mockitagain.MockTests5",
												"methodName" : "test1",
												"testName" : "",
												"params" : [ "first", "second" ]
											}
										} ]
									} ]
						},
						{
							"text" : "mockthird",
							"type" : "package_node",
							"children" : [
									{
										"text" : "CopyOfCopyOfMockTests",
										"type" : "class_node",
										"children" : [
												{
													"type" : "test_method_node",
													"text" : "test12",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test12",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test13",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test13",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test14",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test14",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test11",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test11",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test10",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test10",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test9",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test9",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test8",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test8",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test7",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test7",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test6",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test6",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test4",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test4",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test5",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test5",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test3",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test3",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test2",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test2",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test1",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test1",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test51",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test51",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test16",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test16",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test17",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test17",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test18",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test18",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test19",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test19",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test20",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test20",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test21",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test21",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test23",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test23",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test24",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test24",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test25",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test25",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test126",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfCopyOfMockTests",
														"methodName" : "test126",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												} ]
									},
									{
										"text" : "CopyOfMockTests",
										"type" : "class_node",
										"children" : [
												{
													"type" : "test_method_node",
													"text" : "test12",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test12",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test13",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test13",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test14",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test14",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test11",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test11",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test10",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test10",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test9",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test9",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test8",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test8",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test7",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test7",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test6",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test6",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test4",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test4",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test5",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test5",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test3",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test3",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test2",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test2",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test1",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test1",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test51",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test51",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test16",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test16",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test17",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test17",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test18",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test18",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test19",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test19",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test20",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test20",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test21",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test21",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test23",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test23",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test24",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test24",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test25",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test25",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test126",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.CopyOfMockTests",
														"methodName" : "test126",
														"testName" : "",
														"params" : [ "first",
																"second" ]
													}
												} ]
									},
									{
										"text" : "MockTests",
										"type" : "class_node",
										"children" : [
												{
													"type" : "test_method_node",
													"text" : "test3",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.MockTests",
														"methodName" : "test3",
														"testName" : ""
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test2",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.MockTests",
														"methodName" : "test2",
														"testName" : ""
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test1",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.MockTests",
														"methodName" : "test1",
														"testName" : ""
													}
												} ]
									},
									{
										"text" : "MockTests2",
										"type" : "class_node",
										"children" : [
												{
													"type" : "test_method_node",
													"text" : "test3",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.MockTests2",
														"methodName" : "test3",
														"testName" : ""
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test2",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.MockTests2",
														"methodName" : "test2",
														"testName" : ""
													}
												},
												{
													"type" : "test_method_node",
													"text" : "test1",
													"li_attr" : {
														"className" : "com.testng.tests.mockthird.MockTests2",
														"methodName" : "test1",
														"testName" : ""
													}
												} ]
									},
									{
										"text" : "MockTests3",
										"type" : "class_node",
										"children" : [ {
											"type" : "test_method_node",
											"text" : "test1",
											"li_attr" : {
												"className" : "com.testng.tests.mockthird.MockTests3",
												"methodName" : "test1",
												"testName" : "",
												"params" : [ "first", "second" ]
											}
										} ]
									},
									{
										"text" : "MockTests4",
										"type" : "class_node",
										"children" : [ {
											"type" : "test_method_node",
											"text" : "test1",
											"li_attr" : {
												"className" : "com.testng.tests.mockthird.MockTests4",
												"methodName" : "test1",
												"testName" : "",
												"params" : [ "first", "second" ]
											}
										} ]
									},
									{
										"text" : "MockTests5",
										"type" : "class_node",
										"children" : [ {
											"type" : "test_method_node",
											"text" : "test1",
											"li_attr" : {
												"className" : "com.testng.tests.mockthird.MockTests5",
												"methodName" : "test1",
												"testName" : "",
												"params" : [ "first", "second" ]
											}
										} ]
									} ]
						},
						{
							"text" : "Test1",
							"type" : "class_node",
							"children" : [
									{
										"type" : "test_method_node",
										"text" : "testMethod3",
										"li_attr" : {
											"className" : "com.testng.tests.Test1",
											"methodName" : "testMethod3",
											"testName" : ""
										}
									},
									{
										"type" : "test_method_node",
										"text" : "testMethod2",
										"li_attr" : {
											"className" : "com.testng.tests.Test1",
											"methodName" : "testMethod2",
											"testName" : ""
										}
									},
									{
										"type" : "test_method_node",
										"text" : "testMethod1",
										"li_attr" : {
											"className" : "com.testng.tests.Test1",
											"methodName" : "testMethod1",
											"testName" : ""
										}
									},
									{
										"type" : "test_method_node",
										"text" : "testMethod5",
										"li_attr" : {
											"className" : "com.testng.tests.Test1",
											"methodName" : "testMethod5",
											"testName" : ""
										}
									},
									{
										"type" : "test_method_node",
										"text" : "testBroken",
										"li_attr" : {
											"className" : "com.testng.tests.Test1",
											"methodName" : "testBroken",
											"testName" : "",
											"params" : [ "str", "last" ]
										}
									},
									{
										"type" : "test_method_node",
										"text" : "throwExpectedException1ShouldPass",
										"li_attr" : {
											"className" : "com.testng.tests.Test1",
											"methodName" : "throwExpectedException1ShouldPass",
											"testName" : ""
										}
									},
									{
										"type" : "test_method_node",
										"text" : "throwExpectedException2ShouldPass",
										"li_attr" : {
											"className" : "com.testng.tests.Test1",
											"methodName" : "throwExpectedException2ShouldPass",
											"testName" : ""
										}
									} ]
						} ]
			} ]
		} ]
	} ]
};