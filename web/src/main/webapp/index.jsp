<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Guestbook</title>
</head>
<body>
    <h1>Leave a Comment</h1>
    <form action="/comments" method="post">
        Author: <input type="text" name="author" maxlength="64" required><br>
        Text: <textarea name="text" maxlength="1000" required></textarea><br>
        <button type="submit">Submit</button>
    </form>
</body>
</html>
