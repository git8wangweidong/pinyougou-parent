app.service('loginService',function ($http) {

    this.gerUsername = function () {
        return $http.get("../login/username.do");
    }
})