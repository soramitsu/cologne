const winston = require('winston');

let logger = new (winston.Logger)({
    transports: [
        new (winston.transports.Console)(),
        new (winston.transports.File)({
            name: 'error-file',
            filename: './webErrors.log',
            level: 'error',
            humanReadableUnhandledException: true,
            handleExceptions: true,
            json: false,
            maxsize: 5242880, // 5MB
            maxFiles: 5,
        }),
        new (winston.transports.File)({
            name: 'info-file',
            filename: './web.log',
            level: 'debug',
            json: false,
            maxsize: 5242880, // 5MB
            maxFiles: 5,
        })
    ]
});


export {logger}