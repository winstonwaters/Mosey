module.exports = function(app) {
    app.controller('reviewsController', ['$scope', '$http', '$location', 'reviewsService', 'loginService', function($scope, $http, $location, reviewsService, loginService) {

        $scope.reviewList = reviewsService.getAllReviews();
        $scope.username = loginService.getUsername();
        $scope.errorMessage = '';

        $scope.addReview = function() {
            console.log(`send new review ${$scope.reviewText}`);
            return $http({
                method: 'POST',
                url: '/reviews',
                data: {
                    comment: $scope.reviewText
                    // username: 'teammosey'
                }
            }).catch(function(response) {
                console.log('BRANDON', response);
                $scope.errorMessage = response.data.message;
            }).then(function(response) {
                console.log('pina colada', response);
                return reviewsService.getAllReviews();
            })


        };

        $scope.deleteReview = function(index) {
            console.log(index);
            var comment = {
              id: index.id,
              comment: index.comment,
              username: index.username
            }
            console.log(comment);
          return $http({
            method: 'POST',
            url: '/deletereviews',
            data: comment,
          }).then(function(res){
            console.log(res);
            $scope.reviewList.splice(index, 1);
          }).catch(function(response) {
              console.log('BRANDON', response);
              $scope.errorMessage = response.data.message;
          })
          // .then(function(response){
          //   console.log('deletttting this response: ', response);
          // }), function(error){
          //   console.log('delete error');
          // }
            // $scope.reviewList.splice(index, 1);
        };


    }])
}
