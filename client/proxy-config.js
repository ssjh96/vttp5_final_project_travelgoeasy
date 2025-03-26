module.exports = [
    {
        context: ['/api'],
        target: 'http://localhost:3000',
        secure: false,
    }
]
    // /api stops at localhost:3000/api/user
    // /api/** stops at localhost:3000/api/resr/user

    // lh:4200/ -> default whatevre component
    // lh:4200/dog -> dog component
    // lh:4200/api/user -> route to sb lh:3000(4200)/api/user
    // will not fail the CORS validation
    // (*) allows everything but in prod we shld be using the same domain name lh:4200
    