(function(angular) {
	var grid = angular.module('grid', []);
	grid.constant("gridConst", {
		pagnationSize : 10,
		selectedItems : []
	});
	grid.service('gridService', function(gridConst) {
		return {
			renderGrid : function(scope) {
				scope.reqRowNum = scope.rowNum[0] ? scope.rowNum[0] : gridConst.pagnationSize;
				scope.rowCountLb = scope.reqRowNum;
				scope.currPag = 1;
				scope.remPag = Math.ceil(scope.rdata.length / scope.reqRowNum);
				scope.pagNa = {
					'firstBtn' : true,
					'prevBtn' : true,
					'nxtBtn' : false,
					'lastBtn' : false
				};
				scope.temprdata = scope.rdata.slice(0, gridConst.pagnationSize);
			}
		};
	});
	grid.service('editGridService', function() {
		return {
			editJobDetails : function(data) {
				data.$parent.$parent.job = data.row;
				data.$parent.$parent.job.name = data.row.jobName;
				data.$parent.$parent.job.grpName = data.row.jobGroupName;
				data.$parent.$parent.job.desc = '';
				$('#datetimepicker > input').val(data.row.nextExecTime);
			}
		};
	});
	grid
			.directive(
					'ngGrid',
					function($compile, $rootScope, gridConst, gridService, editGridService) {
						try {
							return {
								restrict : 'E',
								scope : {
									cdata : '=columnData',
									rdata : '=rowData',
									rowNum : '=rowsPerPage'
								},
								template : '<div class="table-responsive">\
								<div><a class="custom-btn" ui-sref="jobDetails">+</a>\
								<a class="custom-btn" data-ng-click="deleteJobs()">-</a></div>\
								<table class="table table-bordered table-condensed table-hover">\
								<thead><tr>\
								<th><input type="checkbox" class="checkbox-size" data-ng-model="check_all" data-ng-change="toggleAll(this)"/></th>\
								<th data-ng-repeat="col in cdata">{{col}}</th></tr></thead><tbody>\
								<tr data-ng-repeat="row in temprdata">\
									<td><input type="checkbox" class="checkbox-size" data-ng-model="row.checked" data-ng-checked="check_all" data-ng-change="toggleCheck(this)"/></td>\
									<td data-ng-bind="row.id"></td>\
	                                <td data-ng-bind="row.userName"></td>\
									<td><a href="#" data-toggle="tooltip" data-placement="right" title=""\
									    data-ng-bind="row.jobName" data-ng-click="editJobDetails(this)"></a></td>\
									<td data-ng-bind="row.jobGroupName"></td>\
									<td data-ng-bind="row.jobDateTime"></td>\
									<td data-ng-bind="row.nextExecTime"></td>\
	                                <td><select class="form-control"\
	                                  data-ng-init="row.changed = row.changed || row.action[0]"\
	                                 data-ng-options="row for row in row.action" data-ng-model="row.changed"></select>\
	                                </td>\
								</tr>\
								</tbody>\
								<tfoot><tr><td colspan="{{cdata.length+1}}"><button class="btn btn-primary" data-ng-disabled="pagNa.firstBtn" data-ng-click="first()">\
								&lt;&lt;</button>&nbsp;<button class="btn btn-primary" data-ng-disabled="pagNa.prevBtn" data-ng-click="prev()">\
								&lt;</button>&nbsp;<label>Page</label>&nbsp;<span data-ng-bind="currPag"></span>&nbsp;\
								<label>of</label>&nbsp;<span data-ng-bind="remPag"></span>&nbsp;\
								<button class="btn btn-primary" data-ng-disabled="pagNa.nxtBtn" data-ng-click="nxt()">\
								&gt;</button>&nbsp;<button class="btn btn-primary" data-ng-disabled="pagNa.lastBtn" data-ng-click="last()">\
								&gt;&gt;</button>&nbsp;<label>Rows per page</label>&nbsp;\
								<select class="form-control" style="width:65px;display:inline-block" data-ng-init="reqRowNum = rowNum[0] || reqRowNum" data-ng-options="row for row in rowNum" data-ng-model="reqRowNum" data-ng-change="onChangeRowNum()"></select>\
								</td></tr></tfoot></table></div>',
								compile : function(element, attributes) {
									return {
										pre : function(scope, ele, attr) {
											gridService.renderGrid(scope);
											scope.checkRowCount = function() {
												if (scope.rdata.length <= scope.reqRowNum) {
													scope.pagNa.nxtBtn = scope.pagNa.lastBtn = true;
													scope.pagNa.firstBtn = scope.pagNa.prevBtn = true;
												}
											};
											scope.resetGrid = function() {
												return this.rdata.slice(this.reqRowNum * (this.currPag - 1), this.reqRowNum * this.currPag);
											};
											scope.checkCurrPage = function() {
												if (scope.currPag === scope.remPag) {
													scope.pagNa.nxtBtn = scope.pagNa.lastBtn = true;
													scope.pagNa.firstBtn = scope.pagNa.prevBtn = false;
												} else if (scope.currPag === 1) {
													scope.pagNa.nxtBtn = scope.pagNa.lastBtn = false;
													scope.pagNa.firstBtn = scope.pagNa.prevBtn = true;
												} else if (scope.currPag > 1) {
													scope.pagNa.nxtBtn = scope.pagNa.lastBtn = scope.pagNa.firstBtn = scope.pagNa.prevBtn = false;
												}
												this.checkRowCount();
											};
											scope.onChangeRowNum = function() {
												if (scope.currPag === scope.remPag) {
													this.remPag = Math.ceil(scope.rdata.length / scope.reqRowNum);
													this.currPag = this.remPag;
												} else {
													this.remPag = Math.ceil(scope.rdata.length / scope.reqRowNum);
													this.currPag = 1;
												}
												this.temprdata = this.resetGrid();
												this.checkCurrPage();
											};
											scope.first = function() {
												this.currPag = 1;
												this.temprdata = this.resetGrid();
												this.checkCurrPage();
											};
											scope.prev = function() {
												this.currPag--;
												this.temprdata = this.resetGrid();
												this.checkCurrPage();
											};
											scope.nxt = function() {
												this.currPag++;
												this.temprdata = this.resetGrid();
												this.checkCurrPage();
											};
											scope.last = function() {
												this.currPag = this.remPag;
												this.temprdata = this.resetGrid();
												this.checkCurrPage();
											};
											scope.editJobDetails = function(object) {
												$rootScope.$broadcast('view_job _details', object.row);
											};
											scope.toggleAll = function(obj) {
												if (obj.check_all)
													gridConst.selectedItems = this.rdata.map(function(obj, idx) {
														return obj['clientId'];
													});
												else
													gridConst.selectedItems.length = 0;
											};
											scope.toggleCheck = function(obj) {
												let
												index = gridConst.selectedItems.findIndex(function(arr) {
													return arr === obj.row.clientId;
												});
												if (index > -1) {
													gridConst.selectedItems.splice(index, 1);
												} else
													gridConst.selectedItems.push(obj.row.clientId);
											};
											scope.deleteJobs = function() {
												if (gridConst.selectedItems.length > 0)
													$rootScope.$broadcast('delete_jobs', gridConst.selectedItems);
											}
											scope.checkRowCount();
										},
										post : function(scope, ele, attr) {
											scope.$watch('rdata', function(oldVal, newVal) {
												if (oldVal !== newVal) {
													scope.temprdata = scope.rdata;
													gridService.renderGrid(scope);
													scope.checkRowCount();
												}
											});
										}
									};
								}
							};
						} catch (e) {
							console.error('Error occured in Grid.js : ' + e);
						}
					});

})(angular);