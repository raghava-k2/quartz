angular.module('myApp', [ 'grid', 'gvit', 'ui.router' ]).constant('gridConstants', {
	'ACTION_OPTIONS' : [ 'BLOCKED', 'COMPLETED', 'ERROR', 'NONE', 'NORMAL', 'PAUSED' ],
	'WEEKS' : [ {
		'sunday' : true
	}, {
		'monday' : true
	}, {
		'tuesday' : true
	}, {
		'wednesday' : true
	}, {
		'thrusday' : true
	}, {
		'friday' : true
	}, {
		'saturday' : true
	} ],
	'MONTHS' : [ {
		'jan' : true
	}, {
		'feb' : true
	}, {
		'mar' : true
	}, {
		'apr' : true
	}, {
		'may' : true
	}, {
		'jun' : true
	}, {
		'jul' : true
	}, {
		'aug' : true
	}, {
		'sep' : true
	}, {
		'oct' : true
	}, {
		'nov' : true
	}, {
		'dec' : true
	} ]
}).config([ '$stateProvider', '$urlRouterProvider', function(stateProvider, urlRouterProvider) {
	urlRouterProvider.otherwise('/searchJob');
	stateProvider.state('scheduler', {
		controller : 'schedulerSearchCtrl',
		url : '/searchJob',
		views : {
			mainView : {
				templateUrl : 'view/searchJob.html'
			}
		}
	}).state('jobDetails', {
		controller : 'schedulerJobDetailsCtrl',
		url : '/schedulerJobDetails',
		views : {
			mainView : {
				templateUrl : 'view/createOrReplaceJobDetails.html'
			}
		}
	});
} ]).filter('singleCharUpper', function() {
	return function(result) {
		return result ? result.charAt(0).toUpperCase() : '';
	};
}).filter('firstCharUpper', function() {
	return function(result) {
		return result ? result.charAt(0).toUpperCase() + result.substring(1) : '';
	};
}).service('schedulerService', [ '$http', 'gridConstants', function($http, gridConst) {
	let
	methods = {
		createJob : function(serverData) {
			return $http({
				url : './do/createjob',
				method : 'post',
				data : serverData,
				headers : {
					'content-type' : 'application/json'
				}
			}).success(function(data) {
				return data;
			}).error(function(data) {
				return data;
			});
		},
		getJobdetails : function(searchJobName) {
			return $http({
				url : './do/getjobdetails',
				method : 'get',
				params : {
					jobName : searchJobName
				}
			}).success(function(data) {
				return data;
			}).error(function(data) {
				return data;
			});
		},
		deleteJobs : function(data) {
			return $http({
				url : './do/deletejobs',
				method : 'delete',
				data : data,
				headers : {
					'content-type' : 'application/json'
				}
			}).success(function(data) {
				return data;
			}).error(function(data) {
				return data;
			});
		},
		updateJobs : function(data) {
			return $http({
				url : './do/updatejob',
				method : 'put',
				data : data,
				headers : {
					'content-type' : 'application/json'
				}
			}).success(function(data) {
				return data;
			}).error(function(data) {
				return data;
			});
		},
		setJobDetails : function(data) {
			let
			setWeeks = function(days) {
				let
				weeks = angular.copy(gridConst.WEEKS);
				if (days) {
					if ((days[0] === '*'))
						return weeks;
					else
						weeks.map(function(obj, idx) {
							let
							index = days.findIndex(function(val) {
								return parseInt(val) === (idx + 1);
							});
							if (index == -1)
								obj[Object.keys(obj)[0]] = false;
						});
				}
				return weeks;
			}, setMonths = function(months) {
				let
				tempMonths = angular.copy(gridConst.MONTHS);
				if (months)
					if ((months[0] === '*'))
						return tempMonths;
					else
						tempMonths.map(function(obj, idx) {
							let
							index = months.findIndex(function(val) {
								return parseInt(val) === idx;
							});
							if (index == -1)
								obj[Object.keys(obj)[0]] = false;
						});
				return tempMonths;
			}, job = {
				clientId : data.clientId,
				userName : data.userName,
				name : data.jobName,
				grpName : data.jobGroupName,
				desc : data.jobDescription,
				startTime : data.jobDateTime ? new Date(data.jobDateTime.substring(0, data.jobDateTime.length - 2)) : '',
				endTime : data.jobEndtime ? new Date(data.jobEndtime.substring(0, data.jobEndtime.length - 2)) : '',
				weeks : setWeeks(data.jobExeDays),
				months : setMonths(data.jobExeMonths),
				glName : data.glInfo ? data.glInfo.glFileName : '',
				outFileName : data.glInfo ? data.glInfo.outputFileName : '',
				mapName : data.glInfo ? data.glInfo.mapName : ''
			};
			return job;
		},
		getMessage : function(serverData, msg) {
			let
			status = {};
			if (serverData.data.status === 'success') {
				status.show = true;
				status.message = msg;
			} else {
				status.show = true;
				status.message = serverData.data.msg;
			}
			return status;
		}
	}, gridInfo = {}, update = {};
	return {
		createJob : methods.createJob,
		getJobdetails : methods.getJobdetails,
		setJobDetails : methods.setJobDetails,
		deleteJobs : methods.deleteJobs,
		updateJobs : methods.updateJobs,
		getMessage : methods.getMessage,
		getGridInfo : function() {
			return gridInfo;
		},
		setGridInfo : function(info) {
			gridInfo = info;
		},
		setUpdate : function(flag) {
			update = flag;
		},
		getUpdate : function() {
			return update;
		}
	};
} ]).controller('schedulerSearchCtrl', [ '$scope', '$timeout', '$state', 'schedulerService', 'gridConstants', function(scope, timeout, state, schService, gridConst) {
	scope.grid = {};
	scope.grid.columns = [ 'Id', 'User Name', 'Job Name', 'Job Group Name', 'Start Time', 'Next Execute Time', 'Status' ];
	scope.grid.rowNum = [ 10, 20, 30, 40, 50 ];
	scope.grid.rows = [];
	scope.showGrid = false;
	scope.loading = {
		isLoading : false,
		size : '50px'
	};
	scope.getAllJobDetails = function() {
		scope.count = 0;
		scope.loading.isLoading = true;
		if (!scope.deleteFlag)
			scope.status = {
				show : false
			};
		schService.getJobdetails(scope.searchJobNames).then(function(data) {
			scope.grid.rows = [];
			if (data.data.length !== 0)
				data.data.map(function(obj, idx) {
					scope.grid.rows.push(obj);
					scope.grid.rows[idx].id = (idx + 1);
					scope.grid.rows[idx].action = angular.copy(gridConst.ACTION_OPTIONS);
					scope.grid.rows[idx].changed = scope.grid.rows[idx].status;
				});
			scope.showGrid = true;
			scope.loading.isLoading = false;
			scope.deleteFlag = false;
		});
		scope.$on('view_job_details', function(event, data) {
			console.log('inside upadate method');
			schService.setUpdate({
				flag : true
			});
			schService.setGridInfo(data);
			state.go('jobDetails');
		});
		scope.$on('delete_jobs', function(event, data) {
			if (scope.count == 0) {
				scope.count = 1;
				schService.deleteJobs(data).then(function(serverData) {
					scope.status = schService.getMessage(serverData, "Successfully deleted Jobs : " + data);
					scope.deleteFlag = true;
					scope.getAllJobDetails();
				});
			}
		});
	};
} ]).controller('schedulerJobDetailsCtrl', [ '$scope', 'schedulerService', '$state', 'gridConstants', function(scope, schService, state, gridConst) {
	scope.job = {
		weeks : angular.copy(gridConst.WEEKS),
		months : angular.copy(gridConst.MONTHS)
	};
	scope.status = {
		show : false,
		message : ''
	};
	scope.createNewJob = function() {
		let
		findDays = function findDays() {
			let
			days = [], count = 0;
			scope.job.weeks.map(function(obj, idx) {
				if (obj[Object.keys(obj)[0]])
					days[count++] = idx + 1;
			});
			return days;
		}, findMonths = function findMonths() {
			let
			months = [], count = 0;
			scope.job.months.map(function(obj, idx) {
				if (obj[Object.keys(obj)[0]])
					months[count++] = idx;
			});
			return months;
		}, trimDate = function trimDate(obj) {
			return obj ? obj.toString().substring(0, obj.toString().lastIndexOf(':') + 3) : '';
		}, data = {
			'clientId' : this.job.clientId,
			'userName' : this.job.userName,
			'jobName' : this.job.name,
			'jobGroupName' : this.job.grpName,
			'jobDescription' : this.job.desc,
			'jobDateTime' : trimDate(this.job.startTime),
			'jobEndtime' : trimDate(this.job.endTime),
			'jobExeDays' : findDays(),
			'jobExeMonths' : findMonths(),
			'glInfo' : {
				'glFileName' : this.job.glName,
				'outputFileName' : this.job.outFileName,
				'mapName' : this.job.mapName
			}
		};
		if (schService.getUpdate().flag === true)
			schService.updateJobs(data).then(function(serverData) {
				scope.status = schService.getMessage(serverData, "Successfully updated Job : " + scope.job.name);
			});
		else
			schService.createJob(data).then(function(serverData) {
				scope.status = schService.getMessage(serverData, "Successfully created Job : " + scope.job.name);
			});
	};
	scope.goBack = function() {
		schService.getUpdate().flag = false;
		state.go('scheduler');
	};
	if (schService.getUpdate().flag === true) {
		scope.job = schService.setJobDetails(schService.getGridInfo());
	}
} ]);