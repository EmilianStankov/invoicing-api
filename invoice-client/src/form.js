import * as React from 'react';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';

export default function InvoiceForm() {
  const [exchangeRates, setExchangeRates] = React.useState("");
  const [outputCurrency, setOutputCurrency] = React.useState("");
  const file = React.createRef();
  const [customerVat, setCustomerVat] = React.useState(null);
  const [success, setSuccess] = React.useState(false);
  const [showSnackbar, setShowSnackbar] = React.useState(false);
  const [errorMessage, setErrorMessage] = React.useState(false);

  const hideSnackbar = () => {
    setShowSnackbar(false);
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
      }
    }).catch(error => {
      setShowSnackbar(true);
      setSuccess(false);
      setErrorMessage("");
    })
  };

  return (
    <React.Fragment>
      <Typography variant="h6" gutterBottom>
        Configuration
      </Typography>
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <TextField
            required
            id="exchangeRates"
            name="exchangeRates"
            label="Exchange Rates"
            fullWidth
            variant="standard"
            value={exchangeRates}
            onChange={e => setExchangeRates(e.target.value)}
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            required
            id="outputCurrency"
            name="outputCurrency"
            label="Output Currency"
            fullWidth
            variant="standard"
            value={outputCurrency}
            onChange={e => setOutputCurrency(e.target.value)}
          />
        </Grid>
        <Grid item xs={12}>
          <Button
            variant="contained"
            component="label"
          >
            Upload CSV
            <input
              type="file"
              ref={file}
              hidden
            />
          </Button>
        </Grid>
        <Grid item xs={12}>
          <TextField
            id="customerVat"
            name="customerVat"
            label="Customer VAT"
            fullWidth
            variant="standard"
            value={customerVat}
            onChange={e => setCustomerVat(e.target.value)}
          />
        </Grid>
        {
          showSnackbar &&
            <Snackbar open={showSnackbar} autoHideDuration={6000} onClose={hideSnackbar}>
              <Alert severity={success ? "success" : "error"}>
                { success ? "Invoice submitted successfully!" : "Failed to submit invoice! " + errorMessage }
                <IconButton
                  size="small"
                  aria-label="close"
                  color="inherit"
                  onClick={hideSnackbar}
                >
                  <CloseIcon fontSize="small" />
                </IconButton>
              </Alert>
            </Snackbar>
        }
        <Grid item xs={12}>
          <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              onClick={submitInvoice}
              variant="contained"
              component="label"
            >
              Submit
            </Button>
          </Box>
        </Grid>
      </Grid>
    </React.Fragment>
  );
}