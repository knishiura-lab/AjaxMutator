window.MutationDetailViewer = (function () {
    function MutationDeteilViewer() {
    }
    MutationDeteilViewer.prototype.setHtmlById = function (html, id) {
        document.getElementById(id).innerHTML = html;
        return this;
    };

    MutationDeteilViewer.prototype.setFileName = function (fileName) {
        return this.setHtmlById(fileName, "fileName");
    };

    MutationDeteilViewer.prototype.setContentBeforeMutation = function (content) {
        return this.setHtmlById(content, "originalFileHead");
    };

    MutationDeteilViewer.prototype.setLines = function (lines) {
        return this.setHtmlById("line " + lines, "modifiedLines");
    };

    MutationDeteilViewer.prototype.setOriginal = function (original) {
        return this.setHtmlById(original, "originalContent");
    };

    MutationDeteilViewer.prototype.setMutated = function (mutated) {
        return this.setHtmlById(mutated, "mutatedContent");
    };

    MutationDeteilViewer.prototype.setContentAfterMutation = function (content) {
        return this.setHtmlById(content, "originalFileFoot");
    };
    return MutationDeteilViewer;
})();


