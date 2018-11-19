app.controller("indexController",function ($scope,loginService) {

    $scope.loadName = function () {
        loginService.gerUsername().success(
            function (data) {
                $scope.loginName =  data.username;
            }
        )
    };

})