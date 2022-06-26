import * as React from 'react';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';
import InvoiceResult from './result';
import InvoiceForm from './form';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';

const steps = ['Submit Invoice', 'Invoice Results'];

export default function InvoiceProcess() {
  const [activeStep, setActiveStep] = React.useState(0);

  const [exchangeRates, setExchangeRates] = React.useState("");
  const [outputCurrency, setOutputCurrency] = React.useState("");
  const file = React.createRef();
  const [customerVat, setCustomerVat] = React.useState(null);
  const [success, setSuccess] = React.useState(false);
  const [showSnackbar, setShowSnackbar] = React.useState(false);
  const [errorMessage, setErrorMessage] = React.useState(false);
  const [invoiceResponse, setInvoiceResponse] = React.useState({});

  const hideSnackbar = () => {
    setShowSnackbar(false);
  }

  const resetProcess = () => {
    setActiveStep(0);
    setExchangeRates("");
    setOutputCurrency("");
    setCustomerVat("");
    setSuccess(false);
    setShowSnackbar(false);
    setErrorMessage(false);
    setInvoiceResponse({});
  }
  
  const submitInvoice = () => {
    const formData  = new FormData();

    formData.append("exchangeRates", exchangeRates);
    formData.append("outputCurrency", outputCurrency);
    formData.append("file", file.current.files[0]);
    if (customerVat != null) {
      formData.append("customerVat", customerVat);
    }

    fetch("http://localhost:8080/api/v1/sumInvoices", {
      method: 'POST',
      body: formData
    }).then(async response => {
      setShowSnackbar(true);
      setSuccess(response.ok);
      if (!response.ok) {
        setErrorMessage(response.status === 404
          ? "VAT number not found!"
          : (await response.json()).message);
        return;
      }
      setInvoiceResponse(await response.json());
      setActiveStep(1);
    }).catch(error => {
      setShowSnackbar(true);
      setSuccess(false);
      setErrorMessage("");
    })
  };

  return (
    <React.Fragment>
      <Box sx={{ p: 2 }}>
        <Stepper activeStep={activeStep}>
          {steps.map(label => (
            <Step key={label}>
              <StepLabel>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>
      </Box>
      {activeStep === 0
        ? <Box>
            <InvoiceForm
              exchangeRates={exchangeRates}
              setExchangeRates={setExchangeRates}
              outputCurrency={outputCurrency}
              setOutputCurrency={setOutputCurrency}
              file={file}
              customerVat={customerVat}
              setCustomerVat={setCustomerVat}/>              
            <Box sx={{ p: 2, display: 'flex', justifyContent: 'flex-end' }}>
              <Button
                onClick={submitInvoice}
                variant="contained"
                component="label"
              >
                Submit
              </Button>
            </Box>
          </Box>
        : <Box>
            <InvoiceResult customers={invoiceResponse.customers} currency={invoiceResponse.currency}/>
            <Box sx={{ p: 2, display: 'flex', justifyContent: 'flex-end' }}>
              <Button
                onClick={resetProcess}
                variant="contained"
                component="label">
                Reset
              </Button>
            </Box>
          </Box>}
      {showSnackbar &&
        <Snackbar open={showSnackbar} autoHideDuration={6000} onClose={hideSnackbar}>
          <Alert severity={success ? "success" : "error"}>
            {success
              ? "Invoice processed successfully!"
              : "Failed to process invoice! " + (errorMessage !== undefined ? errorMessage : "")}
            <IconButton
              size="small"
              aria-label="close"
              color="inherit"
              onClick={hideSnackbar}
            >
              <CloseIcon fontSize="small" />
            </IconButton>
          </Alert>
        </Snackbar>}
    </React.Fragment>
  );
}