# private-group-chat

Latest code in "onprogresschat" branch

Socket URL: ws://localhost:8080/group-chat

In header pass key:event,value:YourGroupName

Rest-End-point: http://localhost:8080/api/chat/send

JSON Data:
{
  "group": "mytestgroup",
  "sender": "Tester",
  "message": "Hello, I'm texting from REST API!"
}
