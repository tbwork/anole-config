'use strict';
var ls = window.localStorage;
var anoleConsoleApp = angular.module('anoleConsoleApp',   ['ngMaterial', 'ngMessages']);  
var loginStatus;
var cur_username;
var lan_index;
var initialized;
var env_initialized;
var permission;
var envs;
var roles;
var projects;
var productLines; 
var users;
var currentProject;
var currentConfig;
var currentEnv;
var searchBarMarginTop_expand = 250;
var searchBarMarginTop_shrink = 0; 

if(ls.getItem("lan_index")!=null)
 lan_index = Number(ls.getItem("lan_index")); 
var nextLanguage = lan_index == 0 ? "English" : "中文";

anoleConsoleApp.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if(event.which === 13) {
                scope.$apply(function (){
                    scope.$eval(attrs.ngEnter);
                });
                event.preventDefault();
            }
        });
    };
});

anoleConsoleApp.filter('cut', function () {
    return function (value, wordwise, max, tail) {
        if (!value) return '';

        max = parseInt(max, 10);
        if (!max) return value;
        if (value.length <= max) return value;

        value = value.substr(0, max);
        if (wordwise) {
            var lastspace = value.lastIndexOf(' ');
            if (lastspace != -1) {
                value = value.substr(0, lastspace);
            }
        }

        return value + (tail || ' ...');
    };
});

function getCookie(name) 
{ 
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
 
    if(arr=document.cookie.match(reg))
 
        return unescape(arr[2]); 
    else 
        return null; 
} 

anoleConsoleApp.factory('GetService', ['$http', '$q', function ($http, $q) {  
	  return {  
	    query : function(input_url) {
	    	var request = new XMLHttpRequest();
	    	request.open('GET', input_url, false);  // `false` makes the request synchronous 
	    	request.onerror=function(error){
	    		showNetErrorAlert("请检查网络后再试！","Please check the network and try again.");
            };
	    	request.send(null); 
	    	if (request.status === 200) {
	    	  var resultJson = angular.fromJson(request.responseText);
	    	  loginStatus = resultJson.loginStatus;
	    	  if(!loginStatus)
	    		  permission = 0;
	    	  return resultJson;
	    	}
	    	else{
	    	  return -1;
	    	}
	    } // end query  
	  };  
	}]);  
anoleConsoleApp.factory('PostService', ['$http', '$q', function ($http, $q) {  
	  return {  
	    submit : function(input_url, data) {  
	    	var request = new XMLHttpRequest(); 
	    	request.open('POST', input_url, false);  // `false` makes the request synchronous 
	    	request.setRequestHeader("dataType", "json");
	    	request.setRequestHeader("Content-Type", "application/json; charset=utf-8");
	    	request.onerror=function(error){
	    		showNetErrorAlert("请检查网络后再试！","Please check the network and try again.");
            };
	    	request.send(angular.toJson(data));
	    	if (request.status === 200) { 
	    		 var resultJson = angular.fromJson(request.responseText);
		    	 return resultJson;
	    	}
	    	else{
	    	  return -1;
	    	}
	    } // end query  
	  };  
	}]);  
anoleConsoleApp.controller('ProjectSearchController', ['GetService', 'PostService',  '$timeout', '$q', '$log', '$http', '$scope', '$mdDialog', '$mdMedia', ProjectSearchController]);  
function ProjectSearchController ( GetService, PostService,  $timeout, $q, $log, $http, $scope, $mdDialog, $mdMedia) { 
    var self = this; 
    loginStatus = loadLoginStatus();
    self.loginStatus = loginStatus;
    self.lan_index = lan_index; 
    self.cur_username = cur_username;
	//initialize the language 
	self.project_searcher_input_placeholder_arr = new Array("请选择项目名","Please select a project.");
	self.user_searcher_input_placeholder_arr = new Array("请选择用户名","Please select a user.");
	self.product_line_searcher_input_placeholder_arr = new Array("请选择产品线","Select a product-line name.");
	self.anole_name_arr = new Array("Anole 企业项目配置管理系统", "Anole Enterprise Configuration Managerment System");
	self.hello_desc_arr = new Array("你好，","Hello, ");
	self.login_desc_arr = new Array("登录", "Log-in");
	self.admin_button_desc_arr = new Array("管理后台","Admin Desk");
	self.product_line_desc_arr = new Array("所属产品线","Product Line");
	self.project_name_desc_arr = new Array("项目名", "Project Name");
	self.project_author_desc_arr  = new Array("创建者","Author");
	self.env_desc_arr = new Array("选择环境","Environment");
	self.in_project_search_box_desc_arr = new Array("条件过滤","Filter By Condition");
	var chResultTitles = new Array("序号","Key名", "环境", "值","描述","上次修改人","操作","项目");
	var enResultTitles = new Array("NO.","Key", "Env","Value","Description","Last Operator","Operation", "Project");
	self.result_title_desc_arr = new Array(chResultTitles,enResultTitles);
	self.out_search_placeholder_desc_arr = new Array("请输入您要查找的Key","Search by key");
	self.in_search_placeholder_desc_arr = new Array("请输入全文过滤条件","Filter Goes Here");
	self.create_new_config_desc_arr = new Array("添加新的配置项","Add a new configuration item.");
	self.modify_config_desc_arr = new Array("修改配置项","Modify a configuration item.");
	self.delete_config_desc_arr = new Array("删除配置项","Delete a configuration item.");
	self.view_config_desc_arr = new Array("查看配置项详情","View details of the configuration item.");
	self.admin_desk_desc_arr = new Array("管理员后台","Admin Desk");
	self.add_new_project_arr = new Array("添加项目","New Project");
	self.add_new_project_desc_arr = new Array("添加新的项目","Add a new project");
	self.add_new_user_arr = new Array("用户管理","User System");
	self.modify_password_arr = new Array("密码管理","Password");
	self.add_new_user_desc_arr = new Array("添加一个新的用户","Add a new Anole user");
	self.modify_password_desc_arr = new Array("修改用户密码","Modify user's password");
	self.initialize_envs_tab_arr = new Array("环境初始化","Environments");
	self.initialize_envs_tab_desc_arr = new Array("初始化设置运行环境","Initialize enviroments.");
	self.power_control_arr = new Array("权限管理","Permission");
	self.power_control_desc_arr = new Array("设置用户权限","Grant a permission to user");
	self.text_username_desc_arr   = new Array("用户名","Username");
	self.text_password_desc_arr   = new Array("密码","Password");
	self.text_product_line_arr   = new Array("产品线","Production Line");
	self.text_power_arr   = new Array("权限","Permission");
	self.text_add_desc_arr = new Array("添加","Add");
	self.text_add_or_update_desc_arr = new Array("添加/修改","Add/Update");
	self.text_update_desc_arr = new Array("修改","Update");
	self.welcome_login_desc_arr = new Array("欢迎登录","Welcome to login."); 
	self.text_confirm_arr = new Array("确认","OK");
	self.text_desc_arr = new Array("描述","Description");
	self.config_desc_note_arr = new Array("配置描述修改对所有环境生效","Modifing description works for all environments");
	self.text_env_arr = new Array("环境","Env");
	self.text_type_arr = new Array("类型","Type");
	self.cancel_confirm_title_arr = new Array("操作确认！","Operation Confirmation!");
	self.cancel_confirm_content_arr = new Array("您确认删除这条配置吗？其对应的所有环境的配置都会被删除！","Are you sure to delete this configuration? It will be deleted from all of the environments!");
	self.config_detail_desc_arr = new Array("配置详情","Configuration Details");
	self.config_edit_desc_arr = new Array("配置修改","Update Configuration");
	self.config_add_desc_arr = new Array("配置添加", "Add Configuration");
	self.config_value_desc_arr = new Array("变量值", "Value");
	self.text_envs_placeholder_desc_arr = new Array("格式：环境变量缩写，多个请用英文逗号隔开", "Set envs here, use comma to split multiple envs")
	self.text_envs_desc_arr = new Array("环境集","Env Set");
	self.new_prd_line_desc_arr = new Array("新的产品线!","New product line!");
	self.logout_desc_arr = new Array("登出", "Logout");
	self.all_env_desc_arr = new Array("所有环境", "All Envs"); 
    self.search_result_desc_arr = new Array("","");//模糊搜索结果描述中英文 
	self.refreshLanguage = function(){
    	self.project_searcher_input_placeholder = self.project_searcher_input_placeholder_arr[self.lan_index];
    	self.user_searcher_input_placeholder = self.user_searcher_input_placeholder_arr[self.lan_index];
    	self.product_line_searcher_input_placeholder = self.product_line_searcher_input_placeholder_arr[self.lan_index];
    	self.anole_name = self.anole_name_arr[self.lan_index];
    	self.hello_desc = self.hello_desc_arr[self.lan_index];
    	self.login_desc = self.login_desc_arr[self.lan_index];
    	self.admin_button_desc = self.admin_button_desc_arr[self.lan_index];
    	self.product_line_desc = self.product_line_desc_arr[self.lan_index];
    	self.project_name_desc = self.project_name_desc_arr[self.lan_index];
    	self.project_author_desc = self.project_author_desc_arr[self.lan_index];
    	self.env_desc = self.env_desc_arr[self.lan_index];
    	self.in_project_search_box_desc = self.in_project_search_box_desc_arr[self.lan_index];
    	self.result_title_desc = self.result_title_desc_arr[self.lan_index];
    	self.out_search_placeholder_desc = self.out_search_placeholder_desc_arr[self.lan_index];
    	self.in_search_placeholder_desc = self.in_search_placeholder_desc_arr[self.lan_index];
    	self.create_new_config_desc = self.create_new_config_desc_arr[self.lan_index];
    	self.modify_config_desc = self.modify_config_desc_arr[self.lan_index];
    	self.delete_config_desc = self.delete_config_desc_arr[self.lan_index];
    	self.view_config_desc = self.view_config_desc_arr[self.lan_index];
    	self.admin_desk_desc = self.admin_desk_desc_arr[self.lan_index];
    	self.add_new_project = self.add_new_project_arr[self.lan_index];
    	self.add_new_project_desc = self.add_new_project_desc_arr[self.lan_index];
    	self.add_new_user = self.add_new_user_arr[self.lan_index];
    	self.add_new_user_desc = self.add_new_user_desc_arr[self.lan_index];
    	self.power_control = self.power_control_arr[self.lan_index];
    	self.power_control_desc = self.power_control_desc_arr[self.lan_index];
    	self.text_username_desc   = self.text_username_desc_arr[self.lan_index];
		self.text_password_desc   = self.text_password_desc_arr[self.lan_index];
		self.text_product_line   = self.text_product_line_arr[self.lan_index];
		self.text_power   = self.text_power_arr[self.lan_index];
		self.text_add_desc = self.text_add_desc_arr[self.lan_index];
		self.text_update_desc = self.text_update_desc_arr[self.lan_index];
		self.welcome_login_desc = self.welcome_login_desc_arr[self.lan_index];
		self.text_confirm = self.text_confirm_arr[self.lan_index];
		self.text_desc = self.text_desc_arr[self.lan_index];
		self.text_env = self.text_env_arr[self.lan_index];
		self.text_type = self.text_type_arr[self.lan_index];
		self.cancel_confirm_title = self.cancel_confirm_title_arr[self.lan_index];
		self.cancel_confirm_content = self.cancel_confirm_content_arr[self.lan_index];
		self.config_detail_desc = self.config_detail_desc_arr[self.lan_index];
		self.config_edit_desc =  self.config_edit_desc_arr[self.lan_index];
		self.config_add_desc = self.config_add_desc_arr[self.lan_index];
		self.config_value_desc = self.config_value_desc_arr[self.lan_index];
		self.text_add_or_update_desc = self.text_add_or_update_desc_arr[self.lan_index];
		self.modify_password_desc = self.modify_password_desc_arr[self.lan_index];
		self.modify_password = self.modify_password_arr[self.lan_index];
		self.text_envs_placeholder_desc  = self.text_envs_placeholder_desc_arr[self.lan_index];
		self.text_envs_desc = self.text_envs_desc_arr[self.lan_index];
		self.initialize_envs_tab = self.initialize_envs_tab_arr[self.lan_index];
		self.initialize_envs_tab_desc = self.initialize_envs_tab_desc_arr[self.lan_index];
		//self.new_prd_line_desc = self.new_prd_line_desc_arr[self.lan_index];
		self.logout_desc = self.logout_desc_arr[self.lan_index];
		self.all_env_desc = self.all_env_desc_arr[self.lan_index];
		self.config_desc_note = self.config_desc_note_arr[self.lan_index];
		self.search_result_desc = self.search_result_desc_arr[self.lan_index];
    }

	self.refreshLanguage();
    
    self.nextPageLanguage = nextLanguage;
    self.changeLanguage = function(){
    	if(self.nextPageLanguage == '中文'){
    		self.nextPageLanguage = 'English';
    		self.lan_index = 0;
    		lan_index = 0;
    	}
    	else{
    		self.nextPageLanguage = '中文';
    		self.lan_index = 1;
    		lan_index = 1;
    	} 
    	ls.setItem("lan_index", String(lan_index));
    	self.refreshLanguage();
    }

    //initial codes
    if(!initialized){
    	envs = loadAllEnvs();
    	roles =  loadRoles();
    	projects  = loadAllProjects();
    	productLines = loadAllPrdLines();
    	initialized = true;
    	env_initialized = loadEnvInitialStatus();
    	loadLoginStatus();
    }
    if(env_initialized != null)
    	self.env_initialized = env_initialized;
    else
    	self.env_initialized = false;
    
    self.envs = envs;
    self.roles = roles;
    self.projectRepos  =  projects;
    self.userRepos = users;
    self.prdLineRepos = productLines;
    self.searchBarMarginTop = searchBarMarginTop_expand;
    self.outsearch_env = "All"; 
    //meta data
    self.types = [
    	{
    	  type:"Number",
    	  value:1
    	},
    	{
    	  type:"Bool",
    	  value:2
    	},
    	{
    	  type:"Text",
    	  value:3
    	}
    ];
    currentEnv = self.envs[0]; 
	self.currentEnv = currentEnv; 
    /*
     * Autocomplete Project Component.
     */
    self.simulateQuery = false;
    self.isDisabled    = false;
    self.selectedProjectItem_For_Index_Page  = null;
    self.selectedProjectItem_For_Auth_Page = null;
    self.selectedUserItem_For_Auth_Page = null;
    self.selectedUserItem_For_Modify_Password_Page = null;
    self.selectedPrdLineItem_For_Project_Page = null;
   
    //user autocomplete
    self.querySearch_User = querySearch_User;
    self.selectedItemChange_User = selectedItemChange_User;
    self.searchTextChange_User   = searchTextChange_User; 
    //project autocomplete
    self.querySearch_Project   = querySearch_Project;
    self.selectedItemChange_Project = selectedItemChange_Project;
    self.searchTextChange_Project   = searchTextChange_Project; 
    //product line auto complete
    self.querySearch_PrdLine   = querySearch_PrdLine;
    self.selectedItemChange_PrdLine = selectedItemChange_PrdLine;
    self.searchTextChange_PrdLine   = searchTextChange_PrdLine; 
    
    self.fuzzySearch = fuzzySearch;
    // config search
    self.queryCurrentProjectConfigsByEnv = queryCurrentProjectConfigsByEnv;
    self.refreshCurrentProjectConfigs = refreshCurrentProjectConfigs;
    
    
    //BUSINESS PARAMETERS
    self.currentProject =  currentProject;
    self.currentConfig = currentConfig;
    self.newConfig = {
    	key: "",
    	value:"",
    	type: 3,
    	desc:"",
    };
    self.configListResult = null; //当前项目的配置列表
    self.configSearchResult = null; // 模糊搜索结果
    self.selectedAuthUser  = null;
    self.selectedPrdLine = null;
    
    //配置详情、修改、添加页面
    //用户账户页面：
    self.text_username = null;
    self.text_password = null;
    
    //修改密码页面:
    self.text_username_for_modify_password = null;
    self.text_password_for_modify_password = null ;
    
    //Env 初始化页面
    self.text_envs = null;
    
    //项目添加页面
    self.text_prd_line = null;
    self.text_project_name  = null;
    
    //权限授予页面
    self.user_search_text = null; //选择用户
    self.project_search_text = null; //选择项目 
    self.grant_env_name = "";
    self.grant_role = ""; 
    
    //配置添加页面
    self.allEnvEnabled = false;
    
    /*
     * Project Autocomplete
     */
    function querySearch_Project(query) {
      var results = query ? self.projectRepos.filter( createFilterFor(query) ) : self.projectRepos,
          deferred;
      if (self.simulateQuery) {
        deferred = $q.defer();
        $timeout(function () { deferred.resolve( results ); }, Math.random() * 1000, false);
        return deferred.promise;
      } else {
        return results;
      }
    }

    function searchTextChange_Project(text) {
      $log.info('Text changed to ' + text);
      if(text == null)
    	  selectedItemChange_Project(null);
    }

    function selectedItemChange_Project(item) {
      $log.info('Item changed to ' + JSON.stringify(item));
      var lastProject = currentProject;
      currentProject = item;
      self.currentProject  = currentProject;
      $log.info('Current project is changed to ' + JSON.stringify(self.currentProject)); 
      if(item != null){
    	  self.configListResult = queryConfigsByProject(item.projectName);
    	  self.outsearch_input = null;
    	  self.configSearchResult = null;
      } 
      else{//为空，显示搜索框
    	  self.configListResult = queryConfigsByProject(null); 
    	  self.searchBarMarginTop = searchBarMarginTop_expand;
      }
      if(item !=null && lastProject != self.currentProject){ 
    	  permission = getPermission(); 
      } 
      	
    } 
    
    function getPermission(projectName, env){
    	if( projectName == null && self.currentProject == null)
    		return 0;
    	var tempProjectName = projectName == null ? self.currentProject.projectName: projectName;
    	var tempEnv = env == null ? self.currentEnv : env;  
    	var postData = {  
				project: tempProjectName, 
				env :  tempEnv
		};
    	if(!loginStatus)
    		return 0;
		var response = PostService.submit("/permission/get", postData); 
		self.loginStatus = loginStatus;
		var resultPermission = 0;
		if(response !=null && response != -1 && response.success){
			 resultPermission = response.result;  
		}else{ 
			 var errorInfo = "";
			 if(lan_index == 0)
				 errorInfo = "获取权限失败！";
			 else
				 errorInfo = "Query permission failed!";  
			 $scope.showAlert(ev, errorInfo ,"notify", self.text_confirm);
		}
    	return resultPermission;
    	
    }
    /*
     * User autocomplete
     */
     function querySearch_User(query) {
	      var results = query ? self.userRepos.filter( createFilterFor(query) ) : self.userRepos,
	          deferred;
	      if (self.simulateQuery) {
	        deferred = $q.defer();
	        $timeout(function () { deferred.resolve( results ); }, Math.random() * 1000, false);
	        return deferred.promise;
	      } else {
	        return results;
	      }
    }

    function searchTextChange_User(text) {
      $log.info('Text changed to ' + text);  
    }

    function selectedItemChange_User(item) {
      $log.info('Item changed to ' + JSON.stringify(item));
       self.selectedAuthUser  = item;  
    } 
    
    /*
     * Product Line auto complete
     */
   function querySearch_PrdLine(query) {
      var results = query ? self.prdLineRepos.filter( createFilterFor(query) ) : self.prdLineRepos,
          deferred;
      if (self.simulateQuery) {
        deferred = $q.defer();
        $timeout(function () { deferred.resolve( results ); }, Math.random() * 1000, false);
        return deferred.promise;
      } else {
        return results;
      }
    }
   
    function searchTextChange_PrdLine(text) {
      $log.info('Text changed to ' + text); 
      if(!isProjectExisted(text)) 
    	  self.new_prd_line_desc = self.new_prd_line_desc_arr[lan_index]; 
      else
    	  self.new_prd_line_desc = "";
    }

    function selectedItemChange_PrdLine(item) {
      $log.info('Item changed to ' + JSON.stringify(item));
      self.selectedPrdLine  = item; 
      self.text_prd_line = item;
    } 
    
    function isProjectExisted(project){
    	if(project == null || project == "")
    		return true;
    	if(self.projectRepos == null)
    		return false;
    	for(var i = 0 ; i < self.projectRepos.length; i++ ){
    		if(project == self.projectRepos[i].projectName){
    			return true;
    		}
    	}
    	return false;
    }
    
    function createFilterFor(query) {
      var lowercaseQuery = angular.lowercase(query);

      return function filterFn(item) {
      	
        return item.value ? item.value.indexOf(lowercaseQuery) === 0 : item.indexOf(lowercaseQuery) ===0;
      }; 
    }
 
  
 
     
      /*
       * Dialog control
       */
      $scope.status = '  ';
	  $scope.customFullscreen = $mdMedia('xs') || $mdMedia('sm');
	  $scope.showAlert = function(ev, title, content, label, ok_text) {
	    // Appending dialog to document.body to cover sidenav in docs app
	    // Modal dialogs should fully cover application
	    // to prevent interaction outside of dialog
	    $mdDialog.show(
	      $mdDialog.alert()
	        .parent(angular.element(document.querySelector('#popupContainer')))
	        .clickOutsideToClose(true)
	        .title(title)
	        .textContent(content)
	        .ariaLabel(label)
	        .ok(ok_text)
	        .targetEvent(ev)
	    );
	  };
	  $scope.refreshGlobalValue = function(){
	     self.loginStatus = loginStatus; 
		 self.cur_username = cur_username;
		 self.lan_index = lan_index;
		 self.projectRepos  =  projects;
	     self.prdLineRepos = productLines;
		 if(self.currentProject != null && self.currentProject.projectName!=null && self.currentProject.projectName!="")
			 self.configListResult = queryConfigsByProject(self.currentProject.projectName);
		 if(self.outsearch_input != null && self.outsearch_input!="")
			 fuzzySearch();
	  }
	  $scope.resultNotify = function(content, success, ev , msg){
	  	if(success)
	  	{
	  		if(lan_index == 0)
	  		   $scope.showAlert(ev, "成功提醒", ":) 操作成功！！！","notify","好的");
	  		else
	  		   $scope.showAlert(ev, "Success", ":) Operation is done successfully!!!","notify", "OK");
	  	}
	  	else{
	  		if(lan_index == 0)
	  		   $scope.showAlert(ev, "失败提醒", ":( 操作失败，具体原因："+msg, "notify","好的");
	  		else
	  		   $scope.showAlert(ev, "Failed", ":( Operation failed, details: "+ msg, "notify", "OK");
	  	} 
	  }
	  $scope.showConfirm = function(ev, title, content, label, ok_text, cancel_text, ok_operation, key, cancel_operation) {
	    // Appending dialog to document.body to cover sidenav in docs app
	    var confirm = $mdDialog.confirm()
	          .title(title)
	          .textContent(content)
	          .ariaLabel(label)
	          .targetEvent(ev)
	          .ok(ok_text)
	          .cancel(cancel_text);
	    $mdDialog.show(confirm).then(function() {
	    	 ok_operation(ev, key);
	    }, function() {
	         cancel_operation();
	    });
	  };
	  
	  
	  
	  $scope.showConfirmDelete = function(key, ev){
		  if(permission < 3){
	    	 if(lan_index == 0)
	     		$scope.showAlert(ev,"警告！","无权限!","alert","OK");
	     	 else
	     	    $scope.showAlert(ev,"Warning!","Permission denied!","alert","OK");
	    	 return ;
	      }
	  	  $scope.showConfirm(ev, self.cancel_confirm_title, self.cancel_confirm_content, "cancel", "YES", "BACK",  $scope.deleteConfig, key, function(){});
	  }
	  $scope.showPrompt = function(ev) {
	    // Appending dialog to document.body to cover sidenav in docs app
	    var confirm = $mdDialog.prompt()
	      .title('What would you name your dog?')
	      .textContent('Bowser is a common name.')
	      .placeholder('Dog name')
	      .ariaLabel('Dog name')
	      .initialValue('Buddy')
	      .targetEvent(ev)
	      .ok('Okay!')
	      .cancel('I\'m a cat person');
	    $mdDialog.show(confirm).then(function(result) {
	      $scope.status = 'You decided to name your dog ' + result + '.';
	    }, function() {
	      $scope.status = 'You didn\'t name your dog.';
	    });
	  };
	  
	  
	  $scope.showAdvanced = function(ev) {
	    var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'))  && $scope.customFullscreen;
	    $mdDialog.show({
	      controller: DialogController,
	      templateUrl: 'login.html',
	      parent: angular.element(document.body),
	      targetEvent: ev,
	      clickOutsideToClose:true,
	      fullscreen: useFullScreen
	    })
	    .then(function(answer) {
	      //process answer
	    }, function() {
	      //$scope.status = 'You cancelled the dialog.';
	    	$scope.refreshGlobalValue();
	    	self.refreshCurrentProjectConfigs();
	    });
	    $scope.$watch(function() {
	      return $mdMedia('xs') || $mdMedia('sm');
	    }, function(wantsFullScreen) {
	      $scope.customFullscreen = (wantsFullScreen === true);
	    }); 
	  };
	  $scope.showTabDialog = function(ev) {
		users = loadAllUsers();
	    $mdDialog.show({
	      controller: DialogController,
	      templateUrl: 'admin_desk.html',
	      parent: angular.element(document.body),
	      targetEvent: ev,
	      clickOutsideToClose:true
	    })
        .then(function(answer) {
          // $scope.status = 'You said the information was "' + answer + '".';
        }, function() {
        	$scope.refreshGlobalValue();
          // $scope.status = 'You cancelled the dialog.';
        });  
	  };  
	  
	 $scope.showConfigViewEx = function(configInfo, _env, event){
		 var config = {
				 key: configInfo.key,
				 type: configInfo.type,
				 value:configInfo.values[_env],
				 desc: configInfo.desc,
				 env : _env
		 }
		 currentProject =  { 
				 projectName: configInfo.project
		 }
		 $scope.showConfigView(config, event, getPermission(configInfo.project, _env));
	 }
	  
	 $scope.showConfigView = function(item, ev, specifiedPermission) {
		 var tempPermission = specifiedPermission==null? permission : specifiedPermission;
	     if(tempPermission == 0){
	    	 if(lan_index == 0)
	     		$scope.showAlert(ev,"警告！","无权限!","alert","OK");
	     	 else
	     	    $scope.showAlert(ev,"Warning!","Permission denied!","alert","OK");
	    	 return ;
	     }
		 currentConfig = item;
		 self.currentConfig = currentConfig;
	     if(item == null){
	     	if(lan_index == 0)
	     		$scope.showAlert(ev,"警告！","配置不存在.","alert","OK");
	     	else
	     	    $scope.showAlert(ev,"Warning!","The configuration is not existed.","alert","OK");
	     } 
	     var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'))  && $scope.customFullscreen;
	    $mdDialog.show({
	      controller: DialogController,
	      templateUrl: 'config_detail.html',
	      parent: angular.element(document.body),
	      targetEvent: ev,
	      clickOutsideToClose:true,
	      fullscreen: useFullScreen
	    })
	    .then(function(answer) {
	     // $scope.status = 'You said the information was "' + answer + '".';
	    }, function() {
	      //$scope.status = 'You cancelled the dialog.';
	    	$scope.refreshGlobalValue();
	    });
	    $scope.$watch(function() {
	      return $mdMedia('xs') || $mdMedia('sm');
	    }, function(wantsFullScreen) {
	      $scope.customFullscreen = (wantsFullScreen === true);
	    });
	    
	 };
	 
	 $scope.showConfigEditWindowEx = function(configInfo, _env, event){
		 var config = {
				 key: configInfo.key,
				 type: configInfo.type,
				 value:configInfo.values[_env],
				 desc: configInfo.desc,
				 env : _env
		 }
		 currentProject = { 
					 projectName: configInfo.project
		 }
		 
		 $scope.showConfigEditWindow(config, event, getPermission(configInfo.project, _env));
	 }
	 
	 
	 $scope.showConfigEditWindow = function(item, ev, specifiedPermission) {
		 var tempPermission = specifiedPermission==null? permission : specifiedPermission;
		 if(tempPermission < 2){
	    	 if(lan_index == 0)
	     		$scope.showAlert(ev,"警告！","无权限!","alert","OK");
	     	 else
	     	    $scope.showAlert(ev,"Warning!","Permission denied!","alert","OK");
	    	 return ;
	     }
	     currentConfig = item;
	     self.currentConfig = currentConfig;
	     if(item == null){
	     	if(lan_index == 0)
	     		$scope.showAlert(ev,"警告！","配置不存在.","alert","OK");
	     	else
	     	    $scope.showAlert(ev,"Warning!","The configuration is not existed.","alert","OK");
	     } 
	     var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'))  && $scope.customFullscreen;
	    $mdDialog.show({
	      controller: DialogController,
	      templateUrl: 'config_edit.html',
	      parent: angular.element(document.body),
	      targetEvent: ev,
	      clickOutsideToClose:true,
	      fullscreen: useFullScreen
	    })
	    .then(function(answer) {
	      //$scope.status = 'You said the information was "' + answer + '".';
	    }, function() {
	     // $scope.status = 'You cancelled the dialog.';
	    	 $scope.refreshGlobalValue();
	    });
	    $scope.$watch(function() {
	      return $mdMedia('xs') || $mdMedia('sm');
	    }, function(wantsFullScreen) {
	      $scope.customFullscreen = (wantsFullScreen === true);
	    });
	   
	 };
	
	 $scope.showConfigAddWindow = function(ev) {  
		 if(permission < 2){
	    	 if(lan_index == 0)
	     		$scope.showAlert(ev,"警告！","无权限!","alert","OK");
	     	 else
	     	    $scope.showAlert(ev,"Warning!","Permission denied!","alert","OK");
	    	 return ;
	     }
	    var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'))  && $scope.customFullscreen;
	    $mdDialog.show({
	      controller: DialogController,
	      templateUrl: 'config_add.html',
	      parent: angular.element(document.body),
	      targetEvent: ev,
	      clickOutsideToClose:true,
	      fullscreen: useFullScreen
	    })
	    .then(function(answer) {
	     // $scope.status = 'You said the information was "' + answer + '".';
	    }, function() {
	     // $scope.status = 'You cancelled the dialog.';
	    	$scope.refreshGlobalValue();
	    });
	    $scope.$watch(function() {
	      return $mdMedia('xs') || $mdMedia('sm');
	    }, function(wantsFullScreen) {
	      $scope.customFullscreen = (wantsFullScreen === true);
	    });
	    
	 };
	//error div show
	self.modify_password_result = "";
	self.modify_password_result_display = "error_hidden";
	self.create_user_result = "";
	self.create_user_result_display = "error_hidden";
	self.login_result = "";
	self.login_result_display = "error_hidden";
	self.initialize_envs_result = "";
	self.initialize_envs_result_display = "error_hidden";
	self.add_project_result = "";
	self.add_project_result_display = "error_hidden";
	self.change_permission_result = "";
	self.change_permission_result_display = "error_hidden";
	self.add_config_result = "";
	self.add_config_result_display = "error_hidden";
	$scope.hiddenError = function(name){
		if(name == 'modify_password'){
			self.modify_passowrd_result_display = "error_hidden";
		}
		if(name == 'create_user'){
			self.create_user_result_display = "error_hidden" ;
		}
		if(name == 'login')
			self.login_result_display = "error_hidden";
		if(name == 'initialize_envs')
			self.initialize_envs_result_display = "error_hidden";
		if(name == 'add_project')
			self.add_project_result_display = "error_hidden";
		if(name == 'change_permission')
			self.change_permission_result_display = "error_hidden";
		if(name == 'add_config')
			self.add_config_result_display = "error_hidden";
	}
	$scope.showError = function(name, error, success){
		if(name == 'modify_password'){
			self.modify_password_result = error;
			self.modify_password_result_display = success ? "error_show_success" :  "error_show_error";
		}
		if(name == 'create_user'){
			self.create_user_result = error;
			self.create_user_result_display = success ? "error_show_success" :  "error_show_error";
		}
		if(name == 'login'){
			self.login_result = error;
			self.login_result_display = success ? "error_show_success" :  "error_show_error";
		}
		if(name == 'initialize_envs'){
			self.initialize_envs_result = error;
			self.initialize_envs_result_display = success ? "error_show_success" :  "error_show_error";
		} 
		if(name == 'add_project'){
			self.add_project_result = error;
			self.add_project_result_display = success ? "error_show_success" :  "error_show_error";
		}
		if(name == 'change_permission'){
			self.change_permission_result = error;
			self.change_permission_result_display = success ? "error_show_success" :  "error_show_error";
		}
		if(name == 'add_config'){
			self.add_config_result = error;
			self.add_config_result_display = success ? "error_show_success" :  "error_show_error";
		}
			 
	}
	
	function adjustLoginStatus(result){ 
	   	 loginStatus = result.loginStatus;
	   	 self.loginStatus = loginStatus;
	   	 if(!loginStatus){
	   		permission = 0;
	   		cur_username = "";
	   		self.cur_username = cur_username;
	   	 }
	   		  
	}
	
	// ----------------------------------------Post Http----------------------------------------------------------
	$scope.assignPermission = function(ev){
		var postData = { 
				operator: cur_username,
				username: self.user_search_text,
				projectName: self.project_search_text,
				env :  self.grant_env_name,
				role: self.grant_role,
		};
		var response = PostService.submit("/permission/assign", postData);
		adjustLoginStatus(response);
		if(response !=null && response != -1 && response.success && response.result.success){
			 var successInfo = ""; 
			 if(lan_index == 0)
				 successInfo = "权限授予成功！";
	  	  	 else
	  	  		 successInfo = "Assign permission successfully!";
			 self.text_prd_line = "";
			 self.text_project_name = "";
			 $scope.loadSpecifiedMetaData(new Array("getProjects"));
			 $scope.showError('change_permission', successInfo, true);
		}else{ 
			 var errorInfo = "";
			 if(lan_index == 0)
				 errorInfo = "权限授予失败！";
			 else
				 errorInfo = "Assign permission failed!";
			 if(!response.success) 
				 errorInfo = errorInfo + " Details: " + response.errorMessage;
			 else if(!response.result.success)
				 errorInfo = errorInfo + " Details: " + response.result.errorMessage; 
		     $scope.showError('change_permission', errorInfo, false);
		}
		
	} 
	
	$scope.addProject = function(ev){
		var postData = { 
				operator: cur_username,
				productLineName: self.text_prd_line,
				projectName: self.text_project_name,
		};
		var response = PostService.submit("/project/create", postData);
		adjustLoginStatus(response);
		if(response !=null && response != -1 && response.success){
			 var successInfo = ""; 
			 if(lan_index == 0)
				 successInfo = "项目添加成功！";
	  	  	 else
	  	  		 successInfo = "Add project successfully!";
			 self.text_prd_line = "";
			 self.text_project_name = "";
			 $scope.loadSpecifiedMetaData(new Array("getProjects","getPrdLines"));
			 $scope.showError('add_project', successInfo, true);
		}else{ 
			 var errorInfo = "";
			 if(lan_index == 0)
				 errorInfo = "项目添加失败！";
			 else
				 errorInfo = "Add project failed!";
			 if(!response.success) 
				 errorInfo = errorInfo + " Details: " + response.errorMessage;
			 else if(!response.result.success)
				 errorInfo = errorInfo + " Details: " + response.result.errorMessage; 
		     $scope.showError('add_project', errorInfo, false);
		}
	}
	
	$scope.addConfig = function(ev){
		var postData = {
				operator:cur_username,
				key:self.newConfig.key,
				destValue:self.newConfig.value,
				destConfigType:self.newConfig.type,
				project:self.currentProject.projectName,
				env: self.currentEnv,
				allEnvEnabled:self.allEnvEnabled
		};
		var response = PostService.submit("/config/create", postData);
		adjustLoginStatus(response);
		if(response !=null && response != -1 && response.success){
			 var successInfo = ""; 
			 if(lan_index == 0)
				 successInfo = "配置添加成功！";
	  	  	 else
	  	  		 successInfo = "Add config successfully!"; 
			 // 更新本地配置信息
			 // self.configListResult.push(response.result);
			 $scope.showError('add_config', successInfo, true);
		}else{ 
			 var errorInfo = "";
			 if(lan_index == 0)
				 errorInfo = "配置添加失败！";
			 else
				 errorInfo = "Add config failed!";
			 if(!response.success) 
				 errorInfo = errorInfo + " Details: " + response.errorMessage;
			 else if(!response.result.success)
				 errorInfo = errorInfo + " Details: " + response.result.errorMessage; 
		     $scope.showError('add_config', errorInfo, false);
		}
	}
	
	$scope.modifyConfig = function(ev){ 
		var postData = {
				operator:cur_username,
				key:self.currentConfig.key,
				value:self.currentConfig.value,
				configType:self.currentConfig.type,
				project:self.currentProject.projectName,
				env: self.currentEnv,
				description: self.currentConfig.desc,
				allEnvEnabled: self.allEnvEnabled
		};
		var response = PostService.submit("/config/modify", postData);
		adjustLoginStatus(response);
		if(response !=null && response != -1 && response.success){
			 var successInfo = ""; 
			 if(lan_index == 0)
				 successInfo = "配置修改成功！";
	  	  	 else
	  	  		 successInfo = "Modify config successfully!";
			 // 更新本地配置信息
			 if(self.currentProject !=  null){ //更新项目配置列表 
				 var resultConfig = response.result;
				 if(self.configListResult != null){
					 for(config in self.configListResult){
						 if(config.key == resultConfig.key){
							 config.type = resultConfig.type;
							 config.value = resultConfig.value;
							 config.desc = resultConfig.desc;
							 config.lastModifier = resultConfig.lastModifier; 
							 break;
						 }
					 }
				 } 
			 }
			 else{ // 更新搜索结果更新
				 if( self.configSearchResult != null){
					 var resultConfig = response.result;
					 for(config in self.configSearchResult){
						 if(config.key == resultConfig.key){
							 config.type = resultConfig.type;
							 config.desc = resultConfig.desc; 
							 config.values = resultConfig.values;
						 }
					 }
				 }
			 }
			 $scope.showError('add_config', successInfo, true);
		}else{ 
			 var errorInfo = "";
			 if(lan_index == 0)
				 errorInfo = "配置修改失败！";
			 else
				 errorInfo = "Modify config failed!";
			 if(!response.success) 
				 errorInfo = errorInfo + " Details: " + response.errorMessage;
			 else if(!response.result.success)
				 errorInfo = errorInfo + " Details: " + response.result.errorMessage; 
		     $scope.showError('add_config', errorInfo, false);
		}
	}
	
	
	$scope.initializeEnvs = function(ev){
		var postData = { 
				operator: cur_username,
				envs: self.text_envs,
		};
		var response = PostService.submit("/project/setEnvs", postData);
		adjustLoginStatus(response);
		if(response !=null && response != -1 && response.success && response.result.success){
			 var successInfo = ""; 
			 if(lan_index == 0)
				 successInfo = "环境设置成功！";
	  	  	 else
	  	  		 successInfo = "Set enviroments successfully!";
			 self.text_envs = ""; 
			 env_initialized = true;
			 self.env_initialized = true;
			 $scope.showError('initialize_envs', successInfo, true);
		}else{ 
			 var errorInfo = "";
			 if(lan_index == 0)
				 errorInfo = "环境设置失败！";
			 else
				 errorInfo = "Set enviroments failed!";
			 if(!response.success) 
				 errorInfo = errorInfo + " Details: " + response.errorMessage;
			 else if(!response.result.success)
				 errorInfo = errorInfo + " Details: " + response.result.errorMessage; 
		     $scope.showError('initialize_envs', errorInfo, false);
		}
	}
	$scope.modifyPassword = function(ev){
		var postData = { 
				operator: cur_username,
				username: self.text_username_for_modify_password,
				password: self.text_password_for_modify_password
		};
		var response = PostService.submit("/user/modifyPassword", postData);
		adjustLoginStatus(response);
		if(response !=null && response != -1 && response.success && response.result.success){
			 var successInfo = ""; 
			 if(lan_index == 0)
				 successInfo = "修改密码成功！";
	  	  	 else
	  	  		 successInfo = "Update password successfully!";
			 self.text_username_for_modify_password = "";
			 self.text_password_for_modify_password = "";
			 $scope.showError('modify_password', successInfo, true);
		}else{ 
			 var errorInfo = "";
			 if(lan_index == 0)
				 errorInfo = "修改密码失败！";
			 else
				 errorInfo = "Update password failed!";
			 if(!response.success) 
				 errorInfo = errorInfo + " Details: " + response.errorMessage;
			 else if(!response.result.success)
				 errorInfo = errorInfo + " Details: " + response.result.errorMessage; 
		     $scope.showError('modify_password', errorInfo, false);
		}
	}
	 
    $scope.logout = function(ev){
    	loginStatus = false;
        self.loginStatus = loginStatus; 
        permission = 0; //退出登录后，权限必定为0
        cur_username = "";
        self.cur_username = cur_username;
        $http.get("/user/logout").then(function successCallback(response) {
    	    if(response.status == 200){
    	    	 $log.info("Logged out!!!");
    	    	 self.refreshCurrentProjectConfigs();
    	    	 fuzzySearch();
    	    }
    	  }, function errorCallback(response) {
    	    // called asynchronously if an error occurs
    	    // or server returns response with an error status.
    	  });
    }
	
	$scope.login = function(ev){
		var projectName = null; 
		if(self.currentProject != null)
			projectName = self.currentProject.projectName; 
		var postData = { 
				username: self.text_username,
				password: self.text_password,
				project: projectName, 
				env :  self.currentEnv 
		};
		var response = PostService.submit("/user/auth", postData);
		adjustLoginStatus(response);
		if(response !=null && response != -1 && response.success && response.result.pass){
			 loginStatus = true; 
			 cur_username = self.text_username; 
			 permission = response.result.permission;
			 $mdDialog.cancel();
		}else{
			 var errorInfo = "";
			 if(lan_index == 0)
				 errorInfo = "登入失败！";
			 else
				 errorInfo = "Login failed!";
			 if(!response.success) 
				 errorInfo = errorInfo + " Details: " + response.errorMessage;
			 else if(!response.result.success)
				 errorInfo = errorInfo + " Details: " + response.result.errorMessage; 
		     $scope.showError('login', errorInfo, false);
		}
	}
	
	$scope.createUser = function(ev){ 
		var postData = {
				operator: cur_username,
				username: self.text_username,
				password: self.text_password
		};
		var response = PostService.submit("/user/create", postData);
		adjustLoginStatus(response);
		if(response !=null && response != -1 && response.success && response.result.success){
			 var successInfo = ""; 
			 if(lan_index == 0)
				 successInfo = "添加用户成功！";
	  	  	 else
	  	  		 successInfo = "Create user successfully!";
			 self.text_username = "";
			 self.text_password = "";
			 $scope.loadSpecifiedMetaData(new Array("getUsers"));
			 $scope.showError('create_user', successInfo, true);
		}else{ 
			 var errorInfo = "";
			 if(lan_index == 0)
				 errorInfo = "添加用户失败！";
			 else
				 errorInfo = "Create user failed!";
			 if(!response.success) 
				 errorInfo = errorInfo + " Details: " + response.errorMessage;
			 else if(!response.result.success)
				 errorInfo = errorInfo + " Details: " + response.result.errorMessage; 
		     $scope.showError('create_user', errorInfo, false);
		}
	}
	
    function fuzzyQueryBySearchTextAndEnv(_searchText, env){ 
		var postData = {
				operator:cur_username,
				searchText:_searchText,
				env: env
		};
		var response = PostService.submit("/config/fuzzyQueryConfig", postData);
		adjustLoginStatus(response);
		if(response !=null && response != -1 && response.success){
			 var result = response.result;
			 var resultCount = result.length;
			 var costTime = response.costTime; 
			 self.search_result_desc_arr = ["搜索到"+resultCount+"个结果，共耗时"+costTime+"ms", "Count of results : " + resultCount+", costs "+costTime+" ms"];
			 self.search_result_desc = self.search_result_desc_arr[self.lan_index];
			 self.searchBarMarginTop = searchBarMarginTop_shrink;
			 return result;
		}else if(response == -1){  
			showNetErrorAlert("查询失败，请检查网络后再试！","Query failed, please check the network and try again.");
		}
		else if(response.errorMessage !=null){
			showNetErrorAlert(response.errorMessage);
		}  
    } 
	
    function queryConfigsByProAndEnv(project, env_in){ 
    	var postData = {
				operator: cur_username,
				project: project,
				env: env_in
		};
		var response = PostService.submit("/config/getByProjectAndEnv", postData);
		adjustLoginStatus(response);
		if(response !=null && response != -1 && response.success && response.result != null){
			  return response.result;
		}else{ 
			 return [];
		}
    }
    
    
	//-----------------------------------------HTTP GET-------data repository------------------------------------------------ 
	function loadEnvInitialStatus(){
		var data = GetService.query('/project/envstatus');
		adjustLoginStatus(data);
		if(data == -1){
       	 if(lan_index == 0)
	  		   showAlert(ev, "失败提醒", ":( 获取配置初始化情况失败", "notify","好的");
	  	  	 else
	  		   showAlert(ev, "Failed", ":( Error occurs when get envs's intialization status!", "notify", "OK"); 
       	  return null;
        }
        else{
       	  return data.result;
        }  
	}
    function loadAllEnvs(){ 
         var data = GetService.query('/project/envs');   // 返回承诺，这里并不是最终数据，而是访问最终数据的API
         adjustLoginStatus(data);
         if(data == -1){
        	 if(lan_index == 0)
	  		   showAlert(null, "失败提醒", ":( 获取配置环境失败!", "notify","好的");
	  	  	 else
	  		   showAlert(null, "Failed", ":( Error occurs when retrieving envs!", "notify", "OK"); 
        	 return null;
         }
         else{
        	 return data.result;
         }  
    }
    function loadLoginStatus(){
    	var data = GetService.query('/user/status');   // 返回承诺，这里并不是最终数据，而是访问最终数据的API  
    	adjustLoginStatus(data);
        if(data == -1){
       	 if(lan_index == 0)
	  		   showAlert(null, "失败提醒", ":( 调用服务器失败!", "notify","好的");
	  	  	 else
	  		   showAlert(null, "Failed", ":( Error occurs when call service from server!", "notify", "OK"); 
       	 	return false;
        }
        else{
        	var result = data;
        	cur_username = result.result.username;
        	loginStatus = result.result.status;
        	self.loginStatus = loginStatus;
       	    return loginStatus;
        }   
    }
    function loadAllUsers(ev){
    	 var data = GetService.query('/user/users');   // 返回承诺，这里并不是最终数据，而是访问最终数据的API  
    	 adjustLoginStatus(data);
         if(data == -1){
        	 if(lan_index == 0)
	  		   showAlert(null, "失败提醒", ":( 获取配置环境失败!", "notify","好的");
	  	  	 else
	  		   showAlert(null, "Failed", ":( Error occurs when retrieving envs!", "notify", "OK"); 
        	 return null;
         }
         else{
        	 return data.result;
         }  
    }
    function loadAllPrdLines(){
    	var data = GetService.query('/project/prdlines');   // 返回承诺，这里并不是最终数据，而是访问最终数据的API  
    	adjustLoginStatus(data);
        if(data == -1){
        	 if(lan_index == 0)
	  		   showAlert(null, "失败提醒", ":( 获取产品线失败!", "notify","好的");
	  	  	 else
	  		   showAlert(null, "Failed", ":( Error occurs when retrieving product lines!", "notify", "OK"); 
        	return null;
        }
        else{
       	 	return data.result;
        }  
    }
    function loadRoles(){
    	var roles = ["stranger", "vistor", "manager", "owner"];
    	return roles;
    }
    function loadAllProjects() {
      var repos = null;
      var data = GetService.query('/project/projects');   // 返回承诺，这里并不是最终数据，而是访问最终数据的API  
      adjustLoginStatus(data);
      if(data == -1){
     	 if(lan_index == 0)
	  		   showAlert(null, "失败提醒", ":( 获取项目列表失败!", "notify","好的");
	  	  	 else
	  		   showAlert(null, "Failed", ":( Error occurs when retrieving projects information!", "notify", "OK"); 
     	 return null;
      }
      else{
    	  repos = data.result;
      }  
      return repos.map( function (repo) {
        repo.value = repo.projectName.toLowerCase();
        return repo;
      });
    } 
    
    function showAlert(ev, title, content, label, ok_text){
    	 $log.error(title+": " + content);  
    }
    
  
    
    function queryConfigsByProject(project){
    	if(project == null)
    		return null; 
    	var env = self.envs[0]; 
    	currentEnv = env;
    	self.currentEnv = currentEnv;
    	return queryConfigsByProAndEnv(project, env);
    }
    
    function fuzzySearch(){
    	if(!checkEmpty(self.outsearch_input) && !checkEmpty(self.outsearch_env) && self.outsearch_env!="All")
    		self.configSearchResult =  fuzzyQueryBySearchTextAndEnv(self.outsearch_input,self.outsearch_env);
    	else{
    		self.configSearchResult = fuzzyQueryBySearchTextAndEnv(self.outsearch_input); 
    	} 
    }
    
    function checkEmpty(input){
    	return input == null || input == "";
    } 
    
    function showNetErrorAlert(chMsg, enMsg){
    	if(enMsg == null) enMsg = chMsg;
    	if(lan_index == 0)
	  		 showAlert(null, "失败提醒", ":( "+chMsg+"!", "notify","好的");
  	  	else
	  		 showAlert(null, "Failed", ":( "+enMsg+"!", "notify", "OK");  
    }
    function refreshCurrentProjectConfigs(){
    	if(self.currentProject!=null && self.currentProject.projectName!=null)
    		self.configListResult = queryConfigsByProAndEnv(self.currentProject.projectName, self.currentEnv);
    }
    function queryCurrentProjectConfigsByEnv(env){
    	var project = self.currentProject.projectName;
    	currentEnv = env;
    	self.currentEnv = currentEnv;
    	self.configListResult = queryConfigsByProAndEnv(project, env);
    }

    // Data refresh
    $scope.loadSpecifiedMetaData = function(methodArr){
    	$http.post("/multi/load", JSON.stringify(methodArr), "{'Content-Type': 'application/json'}").then(function successCallback(response) {
    	    if(response.status == 200){
    	    	var result = response.data;
    	    	if($scope.contains("getProjects",methodArr)){
    	    		projects = result.result.projects;
        	    	self.projectRepos  =  projects;
    	    	}
    	    	if($scope.contains("getPrdLines",methodArr)){
    	    		productLines = result.result.prdLines;
        	    	self.prdLineRepos = productLines;
    	    	} 
    	    	if($scope.contains("loginStatus",methodArr)){
    	    		loginStatus = result.result.loginStatus;
    	    	    self.loginStatus = loginStatus;
    	    	}
    	    }
    	  }, function errorCallback(response) {
    	    // called asynchronously if an error occurs
    	    // or server returns response with an error status.
    	  });
    }
    
    //----------------------Data Business Operations------------------------------------------
    $scope.deleteConfig = function(ev, key){ 
    	$http.post("/config/load", JSON.stringify(key), "{'Content-Type': 'application/json'}").then(function successCallback(response) {
    	    if(response.status == 200){
    	    	var result = response.data;
    	    	var result_info = null;
    	    	if(result.result.success == true)
    	    		result_info = new Array("删除成功!", "Detele successfully!")
    	    	else
    	    		result_info = new Array("删除失败! "+result.result.errorMessage, "Detele successfully! " + result.result.errorMessage);
    	    	  
 	 	  	 	 $scope.resultNotify(result_info[lan_index], true, ev, "");
    	    }
    	  }, function errorCallback(response) {
    	    // called asynchronously if an error occurs
    	    // or server returns response with an error status.
    	  });
    	
    	  
    }
    
    
    $scope.contains = function(item, arr){
    	if(arr == null)
    		return false;
    	for(var i =0 ; i<arr.length; i++){
    		if(item == arr[i])
    			return true;
    	}
    	return false;
    }
  } 
function DialogController($scope, $mdDialog) {
  $scope.hide = function() {
    $mdDialog.hide();
  };
  $scope.cancel = function() {
    $mdDialog.cancel();
  };
  $scope.answer = function(answer) {
    $mdDialog.hide(answer);
  };
}
 