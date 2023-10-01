#ifndef CROPPER_DOC_PDF_SCANNER_H
#define CROPPER_DOC_PDF_SCANNER_H

namespace ptk_scan{

    class PtkScan {
    public:
        virtual ~PtkScan();

        static bool initPtk();

        static bool blk();

    };

}


#endif //CROPPER_DOC_PDF_SCANNER_H
