/*
 * Ext JS Library 2.1
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
Ext.BLANK_IMAGE_URL = 'scripts/extjs/resources/images/default/s.gif';

Ext.onReady(function(){
    Ext.QuickTips.init();
    
    var win;
    
    function getURL(){
        return 'docPrivilege.do?cmd=query&docid=' + Ext.get('doc').getValue();
    }
    
    // renderer, format Read
    function formatRead(value) {
        return value ? "Read" : "";
    }
    
    function formatWrite(value) {
        return value ? "Write" : "";
    }
    
    function formatType(value) {
    	if (value == 'Group')
    		return 'grant.group'.localize();
    	else
    		return 'grant.user'.localize();        
    }
    
    // shorthand alias
    var fm = Ext.form;
    
    var user = Ext.get('user').getValue();
    var author = Ext.get('author').getValue();
	    
    // the column model has information about grid columns
    // dataIndex maps the column to the specific data field in
    // the data store (created below)
    if (user == author) {
	    var checkRead = new Ext.grid.CheckColumn({
						id: 'read',
		       			header: 'grant.read'.localize(),
						dataIndex: 'read', 
						width: 80, 
						editor: new Ext.form.Checkbox(),
						sortable: true
			});	
			
	    var checkWrite = new Ext.grid.CheckColumn({
	           			id: 'write',
		       			header: 'grant.write'.localize(),
	    	   			dataIndex: 'write',
		       			width: 80,
		       			editor: new Ext.form.Checkbox(),
		       			sortable: true
	        });
	        
	    var cm = new Ext.grid.ColumnModel([{
	           id:'name',
	           header: 'grant.name'.localize(),
	           dataIndex: 'name',
	           width: 240
	        },{
	           id:'type',
	           header: 'grant.type'.localize(),
	           renderer: formatType,
	           dataIndex: 'type',
	           width: 100
	        }, 
	        checkRead,
	        checkWrite
	    ]);
	
	    // by default columns are sortable
	    cm.defaultSortable = true;
    
    }
    else {
	    var checkReadNoEdit = new Ext.grid.CheckColumn({
						id: 'read',
		       			header: 'grant.read'.localize(),
						dataIndex: 'read', 
						width: 80,
						sortable: true
			});	
			
	    var checkWriteNoEdit = new Ext.grid.CheckColumn({
	           			id: 'write',
		       			header: 'grant.write'.localize(),
	    	   			dataIndex: 'write',
		       			width: 80,	       			
		       			sortable: true
	        });
	        
	    var cmNoEdit = new Ext.grid.ColumnModel([{
	           id:'name',
	           header: 'grant.name'.localize(),
	           dataIndex: 'name',
	           width: 240
	        },{
	           id:'type',
	           header: 'grant.type'.localize(),
	           renderer: formatType,
	           dataIndex: 'type',
	           width: 100
	        }, 
	        checkReadNoEdit,
	        checkWriteNoEdit
	    ]);
	
	    // by default columns are sortable
	    cmNoEdit.defaultSortable = true;

	}
    // this could be inline, but we want to define the Plant record
    // type so we can add records dynamically
    var priv = Ext.data.Record.create([
           {name: 'type', type: 'string'},
           {name: 'name', type: 'string'},
           {name: 'read', type: 'bool'},
           {name: 'write', type: 'bool'}   
      ]);

    // create the Data Store
    var store = new Ext.data.Store({
        // load using HTTP
        proxy: new Ext.data.HttpProxy({
            url: getURL(),
			method: 'POST'
        }),
        reader: new Ext.data.XmlReader({
               record: 'privilege'}, priv),
           	
        sortInfo:{field:'type', direction:'ASC'}
    });

    // create the editor grid
    if (user == author) {
	    var grid = new Ext.grid.EditorGridPanel({
	    	title			: 'grant.title'.localize(),
	        store			: store,
	        cm				: cm,
	        renderTo		: 'docpriv-grid',
	        width			: 550,
	        height			: 300,
	        //autoExpandColumn:'name',
	        frame			: true,
	        clicksToEdit	: 1,
	        collapsible 	: true,
	        collapsed		: true,
	        sm				: new Ext.grid.RowSelectionModel({singleSelect:false}),
	
	        tbar: [
	        	//new Ext.Toolbar.TextItem('Edit Document Privilege:'),
	        	{
		        	xtype:'tbfill'},{
		        	text: 'grant.tip.add'.localize(),
		            icon: 'scripts/extjs/resources/images/default/dd/drop-add.gif',		            
		            cls: 'x-btn-text-icon',
		            tooltip: 'grant.tip.add'.localize(),
		            handler : function(){
		                showTree();
		            }},
	            {
	            	xtype:"tbseparator"
	            },{
	            	text: 'grant.tip.delete'.localize(),
		            icon: 'scripts/extjs/resources/images/default/delete.gif',
		            cls: 'x-btn-text-icon',
		            tooltip: 'grant.tip.delete'.localize(),		            
		            handler: function() {
			            var selected = grid.getSelectionModel().getSelected();//returns record object for the most recently selected
				
						var typeValue = "";
						var nameValue = "";
						
						//row that is in data store for grid
						if(selected){
							var selectedRows = grid.getSelectionModel().selections.items;
							for (var i=0; i<selectedRows.length; i++) {
								typeValue += selectedRows[i].get("type") + ",";
								nameValue += selectedRows[i].get("name") + ",";
							}
							while(selectedRows.length>0) {
								store.remove(selectedRows[0]);				
							}
							typeValue = typeValue.substring(0, typeValue.length - 1);
							nameValue = nameValue.substring(0, nameValue.length - 1);
						}
						else {
							Ext.MessageBox.alert('window.warning'.localize(), 'grant.noselect'.localize());
							return;
						}
						
						Ext.MessageBox.confirm('grant.confirm.title'.localize(),
					    	'grant.confirm'.localize(),
					        function(btn) {
					        	if(btn == 'yes') {
					            	//submit to server
									Ext.Ajax.request( 
									{   
										waitMsg: 'Delete privilege...',
										url: 'docPrivilege.do?cmd=delete',
										params: { 
												type: typeValue, 
												name: nameValue, 
												docid: Ext.get('doc').getValue()
										},							
										
										failure:function(response,options){
											Ext.MessageBox.alert('window.warning'.localize(), 'grant.delete.error'.localize());
											store.reload();	
											// Update the status bar later in code:
											var sb = Ext.getCmp('my-status');
											sb.setStatus({
											    text: 'grant.delete.error'.localize(),
											    iconCls: 'error-icon',
											    clear: true // auto-clear after a set interval
											});			
										},                                      
										success:function(response,options){
											//Ext.MessageBox.alert('Success','Yeah...');
											//store.reload();
											// Update the status bar later in code:
											var sb = Ext.getCmp('my-status');
											sb.setStatus({
											    text: 'grant.status.complete'.localize(),
											    iconCls: 'ok-icon',
											    clear: true // auto-clear after a set interval
											});
										}                                      
									});
					             }
					         }
					     );	
		            }
	        }],
	        
	        bbar: new Ext.StatusBar({
		        id: 'my-status',
		        
		        // defaults to use when the status is cleared:
		        defaultText: 'grant.status.ready'.localize(),
		        defaultIconCls: 'default-icon',
		        
		        // values to set initially:
		        text: 'grant.status.ready'.localize(),
		        iconCls: 'ready-icon'		        
		        
        	})
	    });	    
	   
	    grid.on("afteredit", function(obj) {
	    	var r = obj.record;
			var typeValue = r.get("type");
			var nameValue = r.get("name");
			var readValue = r.get("read");
			var writeValue = r.get("write");
			
			//check off read then check off write
			//check on write then check on read
			if ((obj.field == 'read') && (!readValue)) {
				obj.record.set('write', false);
				writeValue = false;
			}
			
			if ((obj.field == 'write') && (writeValue)) {
				obj.record.set('read', true);
				readValue = true;
			}
				
			
			Ext.Ajax.request({
				scope: this,
				waitMsg: 'Update privilege',
	   			url: 'docPrivilege.do?cmd=save',
	   			success: function(result, request) {
	   				//Ext.MessageBox.alert('Success', 'Data return from the server: '+ result.responseText);
	   				//if (!r.get('read') && !r.get('write'))
	   				//	store.remove(r);
	   				// Update the status bar later in code:
					var sb = Ext.getCmp('my-status');
					sb.setStatus({
					    text: 'grant.status.complete'.localize(),
					    iconCls: 'ok-icon',
					    clear: true // auto-clear after a set interval
					});						               	
	   			},
	   			failure: function(result, request) {
	   				var record = obj.record;
					record.set(obj.field, obj.originalValue);
					//Ext.MessageBox.alert('Failure', result.responseText);
					// Update the status bar later in code:
					var sb = Ext.getCmp('my-status');
					sb.setStatus({
					    text: result.responseText,
					    iconCls: 'error-icon',
					    clear: true // auto-clear after a set interval
					}); 
	   			},
	   			params: { type: typeValue, name: nameValue, read: readValue, write: writeValue, docid: Ext.get('doc').getValue()}
			});
	    }, grid);	    
	    
	}
	else {
		var grid = new Ext.grid.EditorGridPanel({
	    	title			: 'grant.title'.localize(),
	        store			: store,
	        cm				: cmNoEdit,
	        renderTo		: 'docpriv-grid',
	        width			: 550,
	        height			: 300,
	        //autoExpandColumn:'name',
	        frame			: true,
	        clicksToEdit	: 1,
	        collapsible 	: true,
	        collapsed		: true,	        
	
	        tbar: []
	    });
	}
	
	//store for the window list
	var ds = new Ext.data.Store();
	
	if (user == author) {
		//read write checkbox
		var readCheckbox = new Ext.form.Checkbox({
			name: 'Read',
			checked: true
		}); 
		
		var writeCheckbox = new Ext.form.Checkbox({
			name: 'Write'
		}); 
		
		readCheckbox.on('check', function(obj, checked) {
			if (!checked) {
				writeCheckbox.setValue(false);
			}
		});
		
		writeCheckbox.on('check', function(obj, checked) {
			if (checked) {
				readCheckbox.setValue(true);				
			}
		});
		
		//Create window list
		//Define the grid of selected group/user
	    var listcm = new Ext.grid.ColumnModel([{
		           id:'name',
		           header: 'grant.name'.localize(),
		           dataIndex: 'name',
		           width: 120
		        },{
		           id:'type',
		           header: 'grant.type'.localize(),
		           dataIndex: 'type',
		           renderer: formatType,
		           width: 80
		        }
	    ]);
	    listcm.defaultSortable = true;
	    	
	    var list = new Ext.grid.GridPanel({
	        	id				: 'listGrid',
	        	el				: 'votree-grid',
		        store			: ds,
		        cm				: listcm,
		        width			: 310,
		        height			: 280,
		        autoExpandColumn	: 'name',
		        frame			: true,
		        enableDragDrop		: true,
		        ddGroup			: 'selectVO',
		        collapsible		: true,
	        	animCollapse		: false,
		        selModel		: new Ext.grid.RowSelectionModel({singleSelect:false}),
		        
		        
		        tbar: [new Ext.Toolbar.TextItem('grant.list.title'.localize())
		           	//,{xtype:'tbfill'},{
	        		//text: 'grant.tip.delete'.localize(),
		            //icon: 'scripts/extjs/resources/images/default/delete.gif',
		            //cls: 'x-btn-text-icon',
		            //tooltip: 'grant.tip.delete'.localize(),
		            //handler : function(){
		            //    var records = list.getSelectionModel().getSelections();
		            //    if(!records){
		            //        Ext.Msg.alert("Information","Please select target row!");
		            //       return;
		            //    }
		                
		   			//	for (var i=0; i<records.length; i++)
		            //    	ds.remove(records[i]);
		            //}
	        		//}
	        	]
	        	//,bbar: [
	        	//	'page.type.text'.localize(), 'grant.read'.localize(), readCheckbox, 'grant.write'.localize(), writeCheckbox        		
	        	//]
	        });
	        
	    // Tree for the center
			var voTree = new Ext.tree.TreePanel({
				id				 : 'voTree',
				el				 : 'votree-tree',
		        width			 : 310,  
	            height			 : 280,  
	            checkModel		 : 'mutiple',  
	            onlyLeafCheckable: false,  
	            animate			 : false,  
	            rootVisible		 : true,  
	            autoScroll		 : true,
	            enableDrag		 : true,
	            ddGroup			 : 'selectVO',
	            dropConfig		 : {
	            					//ddGroup: 'selectVO', 
	            					el: list.el},
	            selMode			 : new Ext.tree.MultiSelectionModel(),  
		        loader           : new Ext.tree.TreeLoader({
	        		dataUrl		 : 'VOTree.do?cmd=query',
	        		baseAttrs	 : { uiProvider: Ext.tree.TreeCheckNodeUI } 
		   		}),
		        root			 : new Ext.tree.AsyncTreeNode({
			        text		 : Ext.get('vo').getValue(),		        
			        //draggable	 : false,
			        checked		 : false,
			        type		 : 'Group',
			        id			 : Ext.get('vo').getValue()
		    	}),
		    	
		    	tbar: [new Ext.Toolbar.TextItem('grant.tree.title'.localize())
		    			//,{xtype:'tbfill'},{
			       		//text: 'grant.tip.adduser'.localize(),
			            //icon: 'scripts/extjs/resources/images/default/dd/drop-add.gif',
			            //cls: 'x-btn-text-icon',
			            //tooltip: 'grant.tip.adduser'.localize(),
			            //handler : function(){
			            //    addGroupUser(voTree);
				    	//}            
			    		//}
			    ]
		    });
		    
		    function addGroupUser(tree) {
			var nodes = tree.getChecked();
			if (nodes && nodes.length) {
				for(var i=0; i<nodes.length; i++) {
					if(!isAlreadyExist(nodes[i], list.getStore()))
					{
						var p = new priv({
				                    type: nodes[i].attributes.type,
				                    name: nodes[i].attributes.text,
				                    read: readCheckbox.getValue(),
				                    write: writeCheckbox.getValue()
				                });
				        	//list.stopEditing();
				        	list.getStore().insert(0, p);
			        	}
		        	}
	        }
	        else {
	        	Ext.Msg.alert("window.warning".localize(), 'grant.select.noadd'.localize());
			    return;
	        }
		}
		
		//Drop target for the list, drag source is votree
		var target = new Ext.dd.DropTarget(list.getEl(), {
		        ddGroup: list.ddGroup ,
		        notifyDrop: function(dd, e, data){
		            // determine the row
		            var sm = voTree.getSelectionModel();
	
	            	// get the rows of the tree we have selected
		            var node = sm.selNode;
		
		            // put this data into list grid
		            if(!isAlreadyExist(node, list.getStore())) {
			            var p = new priv({
					                    type: node.attributes.type,
					                    name: node.attributes.text,
					                    read: readCheckbox.getValue(),
					                    write: writeCheckbox.getValue()
					                });
					        	//list.stopEditing();
					        list.getStore().insert(0, p);
			          }
			        		            
		            list.getView().refresh();	
			    }
		    });
		         
		//Middle button for control add/delete         
		var buttons = new Ext.form.FormPanel({
			id				: 'buttons',
			width			: 28,  
	        height			: 280,
	        //hideBorders 	: 'true',
	        baseCls			: 'x-panel',
	        //border			: 'false',
	        //bodyBorder		: 'false',
	        //bodyStyle		: 'padding:5px 5px 0',
	        items			: [{	
		        					html: '<br/><br/><br/><br/><br/><br/>'
	        					},
	        					{
		        					xtype: 'button',
					            	icon: 'scripts/extjs/resources/images/default/rightarrow.png',
					            	cls: 'x-btn-icon',
					            	tooltip: 'grant.tip.adduser'.localize(),
					            	handler : function(){
					                			addGroupUser(voTree);
					    					}
				    			}, 
				    			{	
		        					html: '<br/>'
	        					},	        					
				    			{
					    			xtype: 'button',
			           				icon: 'scripts/extjs/resources/images/default/leftarrow.png',
			            			cls: 'x-btn-icon',
			            			tooltip: 'grant.window.delete'.localize(),
			            			handler : function(){
			                		var records = list.getSelectionModel().getSelections();
			                		if(!records || records.length == 0){
			                    		Ext.Msg.alert("window.warning".localize(), 'grant.select.nodelete'.localize());
			                    		return;
			                		}
			                
			   						for (var i=0; i<records.length; i++)
						                	ds.remove(records[i]);
						            }
				    			},
				    			{	
		        					html: '<br/>'
	        					},	        					
				    			{
					    			xtype: 'button',
			           				icon: 'scripts/extjs/resources/images/default/left2arrow.png',
			            			cls: 'x-btn-icon',
			            			tooltip: 'grant.window.deleteall'.localize(),
			            			handler : function(){
			            				ds.removeAll();
						            }
				    			}
				    		]	  
		});
		            
		//Show Tree Window        
		function showTree() {
			//Create temp store
			// create the Data Store		
			ds.removeAll();
			//for( var i=0; i<store.getCount(); i++) {
		    //	ds.insert(0, store.getAt(i));
		    //}
	        
	        list.render();
	        
	        //Render the tree.
			var checked = voTree.getChecked();
	        for(var i=0; i<checked.length; i++) {
	        	checked[i].getUI().checkbox.checked = false;
	        	checked[i].attributes.checked  = false;
	        	//voTree.fireEvent('check', checked[i], false);  
	        }  
	        
	        voTree.render();
			voTree.getRootNode().expand();
			
			//Render read write checkbox
	        readCheckbox.setValue(true);
	        writeCheckbox.setValue(false);
			
	    	if (!win) {
				win = new Ext.Window({ 
		            title: 'grant.window.title'.localize(),
		            el: 'win-votree',
		            closable:true,
		            modal: true,
		            width:670,
		            height:350,
		            minWidth:670,
		            minHeight:350,
		            plain:true,
		            layout: 'column',
		            closeAction:'hide',
		            items: [voTree, buttons, list],
		            
		            bbar : [{
		        			xtype:'tbfill'},{
				            text: 'grant.button.grantread'.localize(),
				            icon: 'scripts/extjs/resources/images/default/dd/drop-add.gif',
				            cls: 'x-btn-text-icon',
				            tooltip: 'grant.button.grant.tip'.localize(),
				            handler : function(){
				                grantReadPriv(list.getStore(), store);
				            }
			            }, {
			            	xtype:"tbseparator"
			            },{
				            text: 'grant.button.grantwrite'.localize(),
				            icon: 'scripts/extjs/resources/images/default/dd/drop-add.gif',
				            cls: 'x-btn-text-icon',
				            tooltip: 'grant.button.grant.tip'.localize(),
				            handler : function(){
				                grantWritePriv(list.getStore(), store);
				            }
			            }, {
			            	xtype:"tbseparator"
			            },{
				            text: 'grant.button.cancel'.localize(),
				            icon: 'scripts/extjs/resources/images/default/cancel.gif',
				            cls: 'x-btn-text-icon',
				            tooltip: 'grant.button.cancel.tip'.localize(),
				            handler : function(){
				            	win.hide();
			            	}            
		        		}]
		        });
	        }
	        win.show();
	        win.toFront();
		}
	
		
		function grantReadPriv(stsrc, stdes) {
			//if (!readCheckbox.getValue() && !writeCheckbox.getValue())// && !renameCheckbox.getValue()) {
			//{
			//	Ext.MessageBox.alert('window.warning'.localize(), 'page.warning.priv'.localize());
			//	return;
			//}
			
			if (stsrc.getCount() == 0) {
				Ext.MessageBox.alert('window.warning'.localize(), 'page.warning.entity'.localize());
				return false;
			}
			
			grid.stopEditing();
		
			for (var i=0; i<stsrc.getCount(); i++) {
				var record = stsrc.data.items[i];
				var position = isExist(record, stdes);
				if(position == -1)
				{
					var p = new priv({
				                   type: record.get('type'),
				                   name: record.get('name'),
				                   read: true,
				                   write: false
				            });	
					record = p;					
					stdes.insert(0, p);
				}
				else {
					var record = stdes.getAt(position);
					record.set('read', true);
					record.set('write', false);
				}	
				Ext.Ajax.request({
					scope: this,
					waitMsg: 'Update privilege',
			   		url: 'docPrivilege.do?cmd=save',
			   		success: function(result, request) {
			   			//Ext.MessageBox.alert('Success', 'Data return from the server: '+ result.responseText);
			   			// Update the status bar later in code:
			   			grid.startEditing(0, 0);
	        			grid.getView().refresh();
			   			win.hide();
						var sb = Ext.getCmp('my-status');
						sb.setStatus({
						    text: 'grant.status.complete'.localize(),
						    iconCls: 'ok-icon',
						    clear: true // auto-clear after a set interval
						});		   				   						               	
			   		},
			   		failure: function(result, request) {
			   			var deleted = stdes.data.items[0];
			   			stdes.remove(deleted);
			   			grid.startEditing(0, 0);
	        			grid.getView().refresh();
	        			win.hide();
	        			// Update the status bar later in code:
						var sb = Ext.getCmp('my-status');
						sb.setStatus({
						    text: result.responseText,
						    iconCls: 'error-icon',
						    clear: true // auto-clear after a set interval
						});		   				 
			   		},
			   		params: { 
			   			type: record.get('type'), name: record.get('name'), read: true, write: false, docid: Ext.get('doc').getValue()
			   		}
				});						        
	        }        	
	        
	        grid.startEditing(0, 0);
	        grid.getView().refresh();
	        
	        return;
	        
		}
		
		function grantWritePriv(stsrc, stdes) {
			//if (!readCheckbox.getValue() && !writeCheckbox.getValue())// && !renameCheckbox.getValue()) {
			//{
			//	Ext.MessageBox.alert('window.warning'.localize(), 'page.warning.priv'.localize());
			//	return;
			//}
			
			if (stsrc.getCount() == 0) {
				Ext.MessageBox.alert('window.warning'.localize(), 'page.warning.entity'.localize());
				return;
			}
			
			grid.stopEditing();
		
			for (var i=0; i<stsrc.getCount(); i++) {
				var record = stsrc.data.items[i];
				var position = isExist(record, stdes);
				if(position == -1)
				{
					var p = new priv({
				                   type: record.get('type'),
				                   name: record.get('name'),
				                   read: true,
				                   write: true
				            });	
					record = p;					
					stdes.insert(0, p);
				}
				else {
					var record = stdes.getAt(position);
					record.set('read', true);
					record.set('write', true);
				}	
				Ext.Ajax.request({
					scope: this,
					waitMsg: 'Update privilege',
			   		url: 'docPrivilege.do?cmd=save',
			   		success: function(result, request) {
			   			//Ext.MessageBox.alert('Success', 'Data return from the server: '+ result.responseText);
			   			// Update the status bar later in code:
			   			grid.startEditing(0, 0);
	        			grid.getView().refresh();
			   			win.hide();
						var sb = Ext.getCmp('my-status');
						sb.setStatus({
						    text: 'grant.status.complete'.localize(),
						    iconCls: 'ok-icon',
						    clear: true // auto-clear after a set interval
						});		   				   						               	
			   		},
			   		failure: function(result, request) {
			   			var deleted = stdes.data.items[0];
			   			stdes.remove(deleted);
			   			grid.startEditing(0, 0);
	        			grid.getView().refresh();
	        			win.hide();
	        			// Update the status bar later in code:
						var sb = Ext.getCmp('my-status');
						sb.setStatus({
						    text: result.responseText,
						    iconCls: 'error-icon',
						    clear: true // auto-clear after a set interval
						});		   				 
			   		},
			   		params: { 
			   			type: record.get('type'), name: record.get('name'), read: true, write: true, docid: Ext.get('doc').getValue()
			   		}
				});						        
	        }        	
	        
	        grid.startEditing(0, 0);
	        grid.getView().refresh();
	        
	        return;
	        
		}
		
		function isAlreadyExist(node, st) {
			for (var i=0; i<st.getCount(); i++) {
				var record = st.getAt(i);
				if ((record.get('type') == node.attributes.type) && (record.get('name') == node.attributes.text)) {
					return true;
				}
			}
			return false;
		}
		
		function isExist(rd, st) {
			for (var i=0; i<st.getCount(); i++) {
				var record = st.getAt(i);
				if ((record.get('type') == rd.get('type')) && (record.get('name') == rd.get('name'))) {
					return i;
				}
			}
			return -1;
		}
	}
	
	//render store of list grid
	store.load();
});