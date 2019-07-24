const {src, dest, series} = require('gulp');
const clean = require('gulp-clean');

function moveJarFile() {
    return src('./target/Gringotts-*-SNAPSHOT.jar')
        .pipe(dest('./testing/plugins'))
}

function cleanJarFile() {
    return src('./testing/plugins/Gringotts-*-SNAPSHOT.jar')
        .pipe(clean())
}

exports.cleanJarFile = cleanJarFile;
exports.moveJarFile = moveJarFile;

exports.default = series(exports.cleanJarFile, exports.moveJarFile);
