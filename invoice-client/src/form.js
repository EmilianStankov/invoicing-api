import * as React from 'react';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';

export default function InvoiceForm() {
  const [exchangeRates, setExchangeRates] = React.useState("");
  const [outputCurrency, setOutputCurrency] = React.useState("");
  const file = React.createRef();
  const [customerVat, setCustomerVat] = React.useState(null);
  
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
    }).then(response => {
      console.log(response)
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