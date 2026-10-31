Feature: API CRUD examples with Gherkin

  Scenario Outline: GET a post by id
    Given base url "https://jsonplaceholder.typicode.com"
    When I send GET request to "/posts/<id>"
    Then response status should be 200
    And response body should contain "\"id\": <id>"

    Examples:
      | id |
      | 1  |
      | 2  |

  Scenario: POST create a post
    Given base url "https://jsonplaceholder.typicode.com"
    When I send POST request to "/posts" with json body:
      """
      {"title":"foo","body":"bar","userId":1}
      """
    Then response status should be 201
    And response body should contain "\"title\""

  Scenario: PUT update a post
    Given base url "https://jsonplaceholder.typicode.com"
    When I send PUT request to "/posts/1" with json body:
      """
      {"id":1,"title":"updated","body":"updated body","userId":1}
      """
    Then response status should be 200
    And response body should contain "\"title\": \"updated\""

  Scenario: PATCH update only title
    Given base url "https://jsonplaceholder.typicode.com"
    When I send PATCH request to "/posts/1" with json body:
      """
      {"title":"patched"}
      """
    Then response status should be 200
    And response body should contain "\"title\": \"patched\""

  Scenario: DELETE a post
    Given base url "https://jsonplaceholder.typicode.com"
    When I send DELETE request to "/posts/1"
    Then response status should be 200

